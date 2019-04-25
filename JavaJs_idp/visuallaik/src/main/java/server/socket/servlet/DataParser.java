package server.socket.servlet;

import server.socket.servlet.parse.general.GeneralParseResult;
import server.socket.servlet.parse.general.GeneralParser;
import server.socket.servlet.parse.meminfo.MemInfoParser;
import server.socket.servlet.parse.netdev.NetParseResult;
import server.socket.servlet.parse.netdev.NetParser;

public class DataParser {
    private GeneralParser generalParser;
    private NetParser netParser;
    private MemInfoParser memInfoParser;

    public DataParser() {
        this.generalParser = new GeneralParser();
        this.netParser = new NetParser();
        this.memInfoParser = new MemInfoParser();
    }

    public NodeParseData parse(String stringdata)
    {
        NodeParseData parseData = new NodeParseData();
        stringdata = stringdata.replace("\\n", "\n");
        try {
            GeneralParseResult generalInfo = generalParser.parseGeneralInfo(stringdata);
            stringdata = generalInfo.restString;
            NetParseResult netInfo = netParser.parseNetData(stringdata);
            stringdata = netInfo.data;
            MemInfoParser.MemInfoParseResult memInfo = memInfoParser.parseData(stringdata);
            parseData.generalInfo = generalInfo.generalInfo;
            parseData.overallInterfaceStatistics = netInfo.interfaces;
            parseData.memInfo = memInfo.memStatistics;
            return parseData;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

}
