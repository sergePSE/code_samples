package server.socket.servlet.parse.netdev;

import java.util.List;

public class NetParseResult{
    public NetParseResult(List<OverallInterfaceStatistics> interfaces, String data) {
        this.interfaces = interfaces;
        this.data = data;
    }
    public List<OverallInterfaceStatistics> interfaces;
    public String data;
}
