package server.socket.servlet.parse.netdev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetParser {
    Pattern netLinePattern;
    public static int NET_STATS_COUNT = 16;
    public NetParser() {
        String copies = "\\s*(\\d+)" + String.join("",
                Collections.nCopies(NET_STATS_COUNT - 1, "\\s+(\\d+)"));
        this.netLinePattern = Pattern.compile("(\\S+):" + copies);
    }

    public class LineParseResult {
        public LineParseResult() {
        }

        public String restData;
        public boolean hasResult;
        public OverallInterfaceStatistics interfaceStatistics;
    }

    private LineParseResult parseLineData(String data)
    {
        LineParseResult lineParseResult = new LineParseResult();
        Matcher matcher = netLinePattern.matcher(data);
        if (!matcher.find())
        {
            lineParseResult.hasResult = false;
            return lineParseResult;
        }
        lineParseResult.interfaceStatistics = new OverallInterfaceStatistics();
        lineParseResult.interfaceStatistics.interfaceName = matcher.group(1);
        lineParseResult.interfaceStatistics.receiveStats =
                new DirectionalInterfaceStatistics(matcher.group(2), matcher.group(3));
        lineParseResult.interfaceStatistics.transmitStats =
                new DirectionalInterfaceStatistics(matcher.group(10), matcher.group(11));
        lineParseResult.restData = data.substring(matcher.end());
        lineParseResult.hasResult = true;
        return lineParseResult;
    }

    public NetParseResult parseNetData(String data) throws  IllegalArgumentException
    {
        List<OverallInterfaceStatistics> overallInterfaceStatistics = new ArrayList<>();
        for(LineParseResult lineParseResult = parseLineData(data); lineParseResult.hasResult;
            lineParseResult = parseLineData(data))
        {
            data = lineParseResult.restData;
            overallInterfaceStatistics.add(lineParseResult.interfaceStatistics);
        }
        return new NetParseResult(overallInterfaceStatistics, data);
    }
}
