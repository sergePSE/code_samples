package dbFill;

import org.junit.Test;
import server.socket.servlet.DataParser;
import server.socket.servlet.NodeParseData;


public class InputParseTest {
    private static String testMessage = DataExamples.MESSAGE_EXAMPLE;
    @Test
    public void testParse(){
        DataParser dataParser = new DataParser();
        NodeParseData parsedData = dataParser.parse(testMessage);
        assert(parsedData.generalInfo.host.equals("wdw1"));
        assert(parsedData.generalInfo.encodeTimeS == 1527584525);
        assert(parsedData.generalInfo.avg1Load == 0);
        assert(parsedData.generalInfo.avg15Load == 0.05);
        assert(parsedData.overallInterfaceStatistics.get(0).interfaceName.equals("eth0"));
        assert(parsedData.overallInterfaceStatistics.get(0).receiveStats.bytesCount == 45120309);
        assert(parsedData.overallInterfaceStatistics.get(0).transmitStats.packetsCount == 116018);
        assert(parsedData.memInfo.vMallocChunk == 1036564);
        assert(parsedData.generalInfo.temperatureuC == 37000);
    }
}
