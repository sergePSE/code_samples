package server.socket.servlet.parse.meminfo;


import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemInfoParser {
    // unfortunately full pattern can not be applied due to possible difference
    private static final String GENERAL_PATTERN =
            "MemTotal:\\s+(\\d+)\\D+\\n" +
            "MemFree:\\s+(\\d+)\\D+\\n" +
            "MemAvailable:\\s+(\\d+)\\D+\\n" +
            "Buffers:\\s+(\\d+)\\D+\\n" +
            "Cached:\\s+(\\d+)\\D+\\n" +
            "SwapCached:\\s+(\\d+)\\D+\\n" +
            "Active:\\s+(\\d+)\\D+\\n" +
            "Inactive:\\s+(\\d+)\\D+\\n" +
            "Active\\(anon\\):\\s+(\\d+)\\D+\\n" +
            "Inactive\\(anon\\):\\s+(\\d+)\\D+\\n" +
            "Active\\(file\\):\\s+(\\d+)\\D+\\n" +
            "Inactive\\(file\\):\\s+(\\d+)\\D+\\n" +
            "Unevictable:\\s+(\\d+)\\D+\\n" +
            "Mlocked:\\s+(\\d+)\\D+\\n" +
            "SwapTotal:\\s+(\\d+)\\D+\\n" +
            "SwapFree:\\s+(\\d+)\\D+\\n" +
            "Dirty:\\s+(\\d+)\\D+\\n" +
            "Writeback:\\s+(\\d+)\\D+\\n" +
            "AnonPages:\\s+(\\d+)\\D+\\n" +
            "Mapped:\\s+(\\d+)\\D+\\n" +
            "Shmem:\\s+(\\d+)\\D+\\n" +
            "Slab:\\s+(\\d+)\\D+\\n" +
            "SReclaimable:\\s+(\\d+)\\D+\\n" +
            "SUnreclaim:\\s+(\\d+)\\D+\\n" +
            "KernelStack:\\s+(\\d+)\\D+\\n" +
            "PageTables:\\s+(\\d+)\\D+\\n" +
            "NFS_Unstable:\\s+(\\d+)\\D+\\n" +
            "Bounce:\\s+(\\d+)\\D+\\n" +
            "WritebackTmp:\\s+(\\d+)\\D+\\n" +
            "CommitLimit:\\s+(\\d+)\\D+\\n" +
            "Committed_AS:\\s+(\\d+)\\D+\\n" +
            "VmallocTotal:\\s+(\\d+)\\D+\\n" +
            "VmallocUsed:\\s+(\\d+)\\D+\\n" +
            "VmallocChunk:\\s+(\\d+)\\D+\\n" +
            "HugePages_Total:\\s+(\\d+)\\D*\\n" +
            "HugePages_Free:\\s+(\\d+)\\D*\\n" +
            "HugePages_Rsvd:\\s+(\\d+)\\D*\\n" +
            "HugePages_Surp:\\s+(\\d+)\\D*\\n" +
            "Hugepagesize:\\s+(\\d+)\\D+\\n";

    public static int NET_STATS_COUNT = 16;
    public MemInfoParser() {


    }

    public class MemInfoParseResult{
        public MemStatistics memStatistics;
        public String restData;
    }
    Pattern memTotalPattern = Pattern.compile("MemTotal:\\s+(\\d+)\\D+");
    Pattern memFreePattern = Pattern.compile("MemFree:\\s+(\\d+)\\D+");
    Pattern memAvailablePattern = Pattern.compile("MemAvailable:\\s+(\\d+)\\D+");
    Pattern mallocTotalPattern = Pattern.compile("VmallocTotal:\\s+(\\d+)\\D+");
    Pattern mallocUsedPattern = Pattern.compile("VmallocUsed:\\s+(\\d+)\\D+");
    Pattern mallocChunkPattern = Pattern.compile("VmallocChunk:\\s+(\\d+)\\D+");


    private static Logger logger = Logger.getLogger("MemInfoParser");

    private int parseValue(Pattern pattern, String data){
        Matcher matcher = pattern.matcher(data);
        if (!matcher.find()) {
            logger.log(Level.INFO, "memory info was not parsed:" + data);
            throw new IllegalArgumentException();
        }
        return Integer.parseInt(matcher.group(1));
    }



    public MemInfoParseResult parseData(String data) throws IllegalArgumentException
    {
        MemInfoParseResult memInfoParseResult = new MemInfoParseResult();
        memInfoParseResult.memStatistics = new MemStatistics();
        memInfoParseResult.memStatistics.memTotal = parseValue(memTotalPattern, data);
        memInfoParseResult.memStatistics.memFree = parseValue(memFreePattern, data);
        memInfoParseResult.memStatistics.memAvailable = parseValue(memAvailablePattern, data);
        memInfoParseResult.memStatistics.vMallocTotal = parseValue(mallocTotalPattern, data);
        memInfoParseResult.memStatistics.vMallocUsed = parseValue(mallocUsedPattern, data);
        memInfoParseResult.memStatistics.vMallocChunk = parseValue(mallocChunkPattern, data);

        memInfoParseResult.restData = data.substring(data.lastIndexOf("kB") + "kB".length());
        return memInfoParseResult;
    }
}
