/*
 *
 *      Year   : 2013-2017
 *      Author : Saba Ahsan
 *               Cristian Morales Vega
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

#include "metrics.h"
#include "helper.h"
#include "youtube-dl.h"
#include <inttypes.h>
extern metrics metric;
extern struct program_arguments program_arguments;
uint minbuffer = MIN_PREBUFFER;

/* If end is true, it means video has finished downloading. The function will only be called at that time to
 * un-pause in case it is stalled and prebuffering.
 * This function controls the playout and measures the stalls. It is called from inside the savetag functions
 * whenever there is a new TS. The savetag function only needs to update metric.TSnow before calling this function.
 */
void checkstall(bool end)
{
	long long timenow = gettimelong();
	/*If more than one stream is being downloaded, they need to be synchronized
	  metric.TSnow should be the smallest TS in the list, since it represents the highest TS that can be played out.*/
	/*SA 18.11.2014: NUMOFSTREAMS set to 2 for AUDIO and VIDEO. Since mm_parser stores timestamps to TSlist even when both streams are
	in same file, so the check for metric.numofstreams shouldn't be done.*/
//	if(metric.numofstreams > 1)
//	{
		metric.TSnow= metric.TSlist[0];
		for(int i=1; i<NUMOFSTREAMS; i++)
		{
			if(metric.TSnow>metric.TSlist[i])
				metric.TSnow=metric.TSlist[i];
		}
//	}
	if(metric.T0 < 0)
	{
		/*initial run, initialize values*/
		metric.T0 = timenow; /* arrival time of first packet*/
		metric.Tmin0= timenow; /*time at which prebuffering started*/
		metric.TS0 = metric.TSnow; /*the earliest timestamp to be played out after prebuffering*/
	}
	/* check if there is a stall, reset time values if there is. Tmin is the start of playout.
	 * if Tmin is -1 this playout has not started so no need to check
	 * Adding 10ms (10000us) for the encoder and whatnots delay*/
	if(metric.Tmin >= 0 && ((double)(metric.TSnow-metric.TS0)*1000) <=(timenow-metric.Tmin))
	{
		metric.Tmin0=metric.Tmin+((metric.TSnow-metric.TS0)*1000);
		metric.Tmin = -1;
		metric.TS0 = metric.TSnow;
#ifdef DEBUG
		printf("Stall has occured at TS: %d and Time: %ld\n", metric.TSnow, metric.Tmin0); //calculate stall duration
#endif

		if(metric.fail_on_stall) {
			metric.errorcode = ERROR_STALL;
		}
	}

	/*if Tmin<0, then video is buffering; check if prebuffer is reached*/
	if(metric.Tmin< 0)
	{
		if(metric.TSnow-metric.TS0 >= minbuffer || end)
		{
			metric.Tmin = timenow;
#ifdef DEBUG
			printf("Min prebuffer has occured at TS: %d and Time: %ld, start time %ld \n", metric.TSnow, timenow, metric.T0);

#endif
			if(metric.initialprebuftime<0)
			{
				metric.initialprebuftime=(double)(metric.Tmin-metric.stime);
				/*stalls need shorter prebuffering, so change minbufer now that initial prebuf is done. */
				minbuffer = MIN_STALLBUFFER;
			}
			else
			{
				if (program_arguments.instantaneous_output)
					printf("youtubeevent12;%ld;%ld;%" PRIu64 ";%.3f\n",(long)gettimeshort(),(long)metric.htime/1000000, metric.TS0, (double)(metric.Tmin-metric.Tmin0)/1000);
				++metric.numofstalls;
				metric.totalstalltime+=(double)(metric.Tmin-metric.Tmin0);
			}
		}
#ifdef DEBUG
		else
			printf("Prebuffered time %d, TSnow %d, TS0 %d \n",metric.TSnow-metric.TS0, metric.TSnow, metric.TS0);
#endif

	}

}

void printvalues()
{
	double mtotalrate=(metric.totalbytes[STREAM_VIDEO] + metric.totalbytes[STREAM_AUDIO])/(metric.etime-metric.stime);

	const char *result;
	if(metric.errorcode == ITWORKED || metric.errorcode == MAXTESTRUNTIME) {
		result = "OK";
	} else {
		result = "FAIL";
	}

	printf("YOUTUBE_QUIC.0;%ld;%s;", (long)metric.htime/1000000,result);
	char *video_id = strstr(metric.link, "v=") + 2;
	printf("%s;", video_id);
	switch(metric.ft)
	{
		case MP4:
			printf("mp4;");
			break;
		case WEBM:
			printf("webm;");
			break;
		case MP4_A:
			printf("mp4dash;");
			break;
		case WEBM_A:
			printf("webmdash;");
			break;
		case FLV:
			printf("flv;");
			break;
		case TGPP:
			printf("3gpp;");
			break;
		default:
			printf("unknown;");
			break;
	}

	printf("%lld;",metric.etime-metric.stime);   //download time
	printf("%d;",metric.numofstalls); //num of stalls
	printf("%.0f;",(metric.numofstalls>0 ? (metric.totalstalltime/metric.numofstalls) : 0)); // av stall duration
	printf("%.0f;",metric.totalstalltime); // total stall time
	printf("%"PRIu64";", metric.TSnow*1000); // duration
	printf("%.0f;",metric.initialprebuftime); // Initial prebuf time
	printf("%.0f;",mtotalrate); //mtotal rate
	printf("VIDEO;%d;%.0f;",metric.url[STREAM_VIDEO].itag, metric.downloadrate[STREAM_VIDEO]);
	printf("%.0f;", metric.totalbytes[STREAM_VIDEO]); //total bytes
	printf("%.0f;", metric.downloadtime[STREAM_VIDEO] * 1000 * 1000);
	//printf("%s;", metric.url[STREAM_VIDEO].url);
	char *hostname_start = strstr(metric.url[STREAM_VIDEO].url, "://");
	char *hostname_end;
	if(hostname_start) {
		hostname_start += 3;
		hostname_end = strstr(hostname_start, "/");
		*hostname_end = 0;
		printf("%s;", hostname_start);
		*hostname_end = '/';
	} else {
		printf(";");
	}
	printf("%s;", metric.cdnip[STREAM_VIDEO]);
	printf("%.0f;", metric.connection_time_s[STREAM_VIDEO] * 1000 * 1000);
	printf("%d;",metric.url[STREAM_VIDEO].bitrate);
	printf("%s;",metric.quic_ver[STREAM_VIDEO]);
	printf("%s;",metric.http_ver[STREAM_VIDEO]);
	printf("AUDIO;%d;%.0f;",metric.url[STREAM_AUDIO].itag,metric.downloadrate[STREAM_AUDIO]);
	printf("%.0f;", metric.totalbytes[STREAM_AUDIO]); //total bytes
	printf("%.0f;", metric.downloadtime[STREAM_AUDIO] * 1000 * 1000);
	//printf("%s;", metric.url[STREAM_AUDIO].url);
	hostname_start = strstr(metric.url[STREAM_AUDIO].url, "://");
	if(hostname_start) {
		hostname_start += 3;
		hostname_end = strstr(hostname_start, "/");
		*hostname_end = 0;
		printf("%s;", hostname_start);
		*hostname_end = '/';
	} else {
		printf(";");
	}
	printf("%s;", metric.cdnip[STREAM_AUDIO]);
	printf("%.0f;", metric.connection_time_s[STREAM_AUDIO] * 1000 * 1000);

	printf("%d;",metric.url[STREAM_AUDIO].bitrate);
	printf("%s;",metric.quic_ver[STREAM_AUDIO]);
	printf("%s;",metric.http_ver[STREAM_AUDIO]);

	printf("%.0f;", metric.first_website_connection_time_s * 1000 * 1000);
	printf("%.0f;",metric.startup+metric.initialprebuftime); /*startup delay*/ 
	printf("%d;",metric.playout_buffer_seconds); /*range*/
	printf("%d;\n",metric.errorcode);

}
