package server.socket.servlet;

import server.socket.servlet.parse.general.GeneralInfo;
import server.socket.servlet.parse.meminfo.MemStatistics;
import server.socket.servlet.parse.netdev.OverallInterfaceStatistics;

import java.util.List;

public class NodeParseData {
    public GeneralInfo generalInfo;
    public List<OverallInterfaceStatistics> overallInterfaceStatistics;
    public MemStatistics memInfo;
}
