import server.socket.servlet.NodeParseData;
import server.socket.servlet.parse.general.GeneralInfo;
import server.socket.servlet.parse.meminfo.MemStatistics;
import server.socket.servlet.parse.netdev.DirectionalInterfaceStatistics;
import server.socket.servlet.parse.netdev.OverallInterfaceStatistics;

import java.util.ArrayList;
import java.util.Calendar;

public class NodeParseDataGenerator {
    public static NodeParseData getParseResult(String nodeName)
    {
        NodeParseData parseResult = new NodeParseData();
        parseResult.generalInfo = new GeneralInfo();
        parseResult.generalInfo.host = nodeName;
        parseResult.generalInfo.avg1Load = Math.random();
        parseResult.generalInfo.avg5Load = Math.random();
        parseResult.generalInfo.avg15Load = Math.random();
        parseResult.generalInfo.encodeTimeS = Calendar.getInstance().getTimeInMillis()/1000;
        parseResult.generalInfo.temperatureuC = (int)(Math.random()*100000);
        parseResult.generalInfo.currentProcesses = (int)(Math.random() * 100);
        parseResult.generalInfo.lastProcessId = (int)(Math.random() * 100000);
        parseResult.generalInfo.totalProcesses = parseResult.generalInfo.currentProcesses + (int)(Math.random() * 100);

        OverallInterfaceStatistics interfaceStatistics = new OverallInterfaceStatistics();
        interfaceStatistics.interfaceName = "eth0";
        interfaceStatistics.transmitStats = new DirectionalInterfaceStatistics((int)(Math.random() * 1000000),
                (int)(Math.random() * 100000));
        interfaceStatistics.receiveStats = new DirectionalInterfaceStatistics((int)(Math.random() * 1000000),
                (int)(Math.random() * 100000));
        parseResult.overallInterfaceStatistics = new ArrayList<>();
        parseResult.overallInterfaceStatistics.add(interfaceStatistics);

        parseResult.memInfo = new MemStatistics();
        parseResult.memInfo.memTotal = (int)(Math.random() * 1000000);
        parseResult.memInfo.memAvailable = (int)(parseResult.memInfo.memTotal * Math.random());
        parseResult.memInfo.memFree = (int)(parseResult.memInfo.memTotal * Math.random());

        parseResult.memInfo.vMallocTotal = (int)(Math.random() * 1000000);
        parseResult.memInfo.vMallocUsed = (int)(parseResult.memInfo.vMallocTotal * Math.random());
        parseResult.memInfo.vMallocChunk = (int)(parseResult.memInfo.vMallocTotal * Math.random());
        return parseResult;
    }
}
