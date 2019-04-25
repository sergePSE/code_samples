import java.text.DecimalFormat;

public class NodeMessageGenerator {
    public static final String MESSAGE_TEMPLATE = "ENCODE_HOST: %name, ENCODE_TIME: %time, CONTENT: b'%double " +
            "%double %double " +
            "%int/%int %int\nInter-|   Receive " +
            "  |  Transmit\n face |bytes    packets errs drop fifo frame compressed " +
            "multicast|bytes    packets errs drop fifo colls carrier compressed\n" +
            "eth0: %int  %int    0    0    0     0          0         0 " +
            "%int  %int    0    0    0     0       0          0\n    lo:" +
            "0       0    0    0    0     0          0         0        0       0 " +
            "0    0    0     0       0          0\nMemTotal:        %int " +
            "kB\nMemFree:         %int kB\nMemAvailable:    %int kB\nBuffers: " +
            "           %int kB\nCached:            %int kB\nSwapCached:            0 " +
            "kB\nActive:            %int kB\nInactive:          %int " +
            "kB\nActive(anon):      %int kB\nInactive(anon):    %int " +
            "kB\nActive(file):       %int kB\nInactive(file):    %int " +
            "kB\nUnevictable:           0 kB\nMlocked:               0 kB\nSwapTotal: " +
            "           0 kB\nSwapFree:              0 kB\nDirty:                 0 " +
            "kB\nWriteback:             0 kB\nAnonPages:         %int kB\nMapped: " +
            "        %int kB\nShmem:             %int kB\nSlab:              %int " +
            "kB\nSReclaimable:      %int kB\nSUnreclaim:        %int " +
            "kB\nKernelStack:        %int kB\nPageTables:          %int " +
            "kB\nNFS_Unstable:          0 kB\nBounce:                0 " +
            "kB\nWritebackTmp:          0 kB\nCommitLimit:     %int " +
            "kB\nCommitted_AS:      %int kB\nVmallocTotal:    %int " +
            "kB\nVmallocUsed:        %int kB\nVmallocChunk:    %int " +
            "kB\nHugePages_Total:       0\nHugePages_Free:        0\nHugePages_Rsvd: " +
            "     0\nHugePages_Surp:        0\nHugepagesize:       %int kB\n%int\n";

    private static String replaceDoubles(String message)
    {
        while(message.contains("%double"))
        {
            DecimalFormat df = new DecimalFormat("0.##");
            message = message.replaceFirst("%double", df.format(Math.random()));
        }
        return message;
    }

    private static String replaceInts(String message)
    {
        while(message.contains("%int"))
        {
            message = message.replaceFirst("%int", Integer.toString((int)(Math.random()*10000)));
        }
        return message;
    }

    public static String generateMessage(String nodeName)
    {
        String message = MESSAGE_TEMPLATE;
        message = message.replaceFirst("%name", nodeName);
        message = message.replaceFirst("%time", Long.toString(System.currentTimeMillis()/1000));
        message = replaceDoubles(message);
        message = replaceInts(message);
        return message;
    }
}
