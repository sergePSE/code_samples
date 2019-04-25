package server.contentObserver;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import server.contentObserver.dataInsertion.GraphDataContextInserter;
import server.contentObserver.dataInsertion.GraphInserter;
import server.contentObserver.dataInsertion.GraphSampleInserter;
import server.hibernate.models.*;
import server.socket.servlet.NodeParseData;
import server.socket.servlet.parse.general.GeneralInfo;
import server.socket.servlet.parse.meminfo.MemStatistics;
import server.socket.servlet.parse.netdev.OverallInterfaceStatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

// inserts data to database
public class ParseResultInserter {

    public ParseResultInserter(Session session) {
        checkGraphExistance(session);
    }

    private void checkGraphExistance(Session session)
    {
        Graph[] necessaryGraphs = {GraphInserter.getAverageGraphFrame(), GraphInserter.getBytesCountGraphFrame(),
                GraphInserter.getMallocGraphFrame(), GraphInserter.getMemoryGraphFrame(),
                GraphInserter.getTemperatureGraphFrame(), GraphInserter.getPacketsCountGraphFrame()
        };
        for (Graph graph : necessaryGraphs) {
            GraphInserter.getOrCreateGraph(session, graph);
        }
    }

    private Node findNode(Session session, String nodeName)
    {
        Query<Node> nodeQuery = session.createQuery("from Node where name = :nodeName");
        nodeQuery.setParameter("nodeName", nodeName);
        Optional<Node> optNode = nodeQuery.stream().findFirst();
        if (optNode.isPresent())
            return optNode.get();
        return null;
    }

    private List<server.websocket.models.GraphSampleData> insertContextData(Session session, Context context,
        HashMap<String, GraphSampleValue> graphSampleValues)
    {
        List<server.websocket.models.GraphSampleData> insertedData = new ArrayList<>();
        for (String graphSampleName : graphSampleValues.keySet()) {
            GraphSample sample = GraphSampleInserter.getOrUpdateGraphSample(session, context, graphSampleName);
            GraphSampleData sampleData = new GraphSampleData();
            sampleData.setGraphSample(sample);
            sampleData.setxValue(graphSampleValues.get(graphSampleName).timeMs);
            sampleData.setyValue(graphSampleValues.get(graphSampleName).value);
            Transaction transaction = session.beginTransaction();
            session.save(sampleData);
            transaction.commit();
            insertedData.add(new server.websocket.models.GraphSampleData(sampleData.getxValue(), sampleData.getyValue(),
                    sample.getId()));
        }
        return insertedData;
    }

    private void notifyNewData(long contextId, List<server.websocket.models.GraphSampleData> insertedData)
    {
        NodeDataObservable nodeDataObservable = NodeDataObservable.getInstance();
        if (nodeDataObservable != null)
            nodeDataObservable.notifyData(contextId, insertedData);
    }

    private void insertAverage(Session session, Node node, GeneralInfo generalInfo, long time)
    {
        Graph graph = GraphInserter.getOrCreateGraph(session, GraphInserter.getAverageGraphFrame());
        Context context = GraphDataContextInserter.getOrUpdateGraphContext(session, graph, node);
        HashMap<String, GraphSampleValue> sampleValues = new HashMap<>();
        sampleValues.put(GraphSampleInserter.AVG_SAMPLES.AVG_1MIN,
                new GraphSampleValue(time, generalInfo.avg1Load));
        sampleValues.put(GraphSampleInserter.AVG_SAMPLES.AVG_5MIN,
                new GraphSampleValue(time, generalInfo.avg5Load));
        sampleValues.put(GraphSampleInserter.AVG_SAMPLES.AVG_15MIN,
                new GraphSampleValue(time, generalInfo.avg15Load));
        List<server.websocket.models.GraphSampleData> insertedData = insertContextData(session, context, sampleValues);
        notifyNewData(context.getId(), insertedData);
    }

    private void insertTemperature(Session session, Node node, GeneralInfo generalInfo, long time)
    {
        Graph graph = GraphInserter.getOrCreateGraph(session, GraphInserter.getTemperatureGraphFrame());
        Context context = GraphDataContextInserter.getOrUpdateGraphContext(session, graph, node);
        HashMap<String, GraphSampleValue> sampleValues = new HashMap<>();
        sampleValues.put(GraphSampleInserter.TEMPERATURE_SAMPLES.TEMP,
                new GraphSampleValue(time, generalInfo.temperatureuC));
        List<server.websocket.models.GraphSampleData> insertedData = insertContextData(session, context, sampleValues);
        notifyNewData(context.getId(), insertedData);
    }

    private void insertBytes(Session session, Node node, OverallInterfaceStatistics interfaceStatistics, long time )
    {
        Graph graph = GraphInserter.getOrCreateGraph(session, GraphInserter.getBytesCountGraphFrame());
        Context context = GraphDataContextInserter.getOrUpdateGraphContext(session, graph, node);
        HashMap<String, GraphSampleValue> sampleValues = new HashMap<>();
        sampleValues.put(GraphSampleInserter.BYTES_SAMPLES.BYTES_IN,
                new GraphSampleValue(time, interfaceStatistics.receiveStats.bytesCount));
        sampleValues.put(GraphSampleInserter.BYTES_SAMPLES.BYTES_OUT,
                new GraphSampleValue(time, interfaceStatistics.transmitStats.bytesCount));
        List<server.websocket.models.GraphSampleData> insertedData = insertContextData(session, context, sampleValues);
        notifyNewData(context.getId(), insertedData);
    }

    private void insertPackets(Session session, Node node, OverallInterfaceStatistics interfaceStatistics, long time)
    {
        Graph graph = GraphInserter.getOrCreateGraph(session, GraphInserter.getPacketsCountGraphFrame());
        Context context = GraphDataContextInserter.getOrUpdateGraphContext(session, graph, node);
        HashMap<String, GraphSampleValue> sampleValues = new HashMap<>();
        sampleValues.put(GraphSampleInserter.PACKETS_SAMPLES.PACKETS_IN,
                new GraphSampleValue(time, interfaceStatistics.receiveStats.packetsCount));
        sampleValues.put(GraphSampleInserter.PACKETS_SAMPLES.PACKETS_OUT,
                new GraphSampleValue(time, interfaceStatistics.transmitStats.packetsCount));
        List<server.websocket.models.GraphSampleData> insertedData = insertContextData(session, context, sampleValues);
        notifyNewData(context.getId(), insertedData);
    }

    private void insertMemStats(Session session, Node node, MemStatistics memStatistics, long time)
    {
        Graph graph = GraphInserter.getOrCreateGraph(session, GraphInserter.getMemoryGraphFrame());
        Context context = GraphDataContextInserter.getOrUpdateGraphContext(session, graph, node);
        HashMap<String, GraphSampleValue> sampleValues = new HashMap<>();
        sampleValues.put(GraphSampleInserter.MEMORY_SAMPLES.MEM_AVAILABLE,
                new GraphSampleValue(time, memStatistics.memAvailable));
        sampleValues.put(GraphSampleInserter.MEMORY_SAMPLES.MEM_FREE,
                new GraphSampleValue(time, memStatistics.memFree));
        sampleValues.put(GraphSampleInserter.MEMORY_SAMPLES.MEM_TOTAL,
                new GraphSampleValue(time, memStatistics.memTotal));
        List<server.websocket.models.GraphSampleData> insertedData = insertContextData(session, context, sampleValues);
        notifyNewData(context.getId(), insertedData);
    }

    private void insertMallocStats(Session session, Node node, MemStatistics memStatistics, long time)
    {
        Graph graph = GraphInserter.getOrCreateGraph(session, GraphInserter.getMallocGraphFrame());
        Context context = GraphDataContextInserter.getOrUpdateGraphContext(session, graph, node);
        HashMap<String, GraphSampleValue> sampleValues = new HashMap<>();
        sampleValues.put(GraphSampleInserter.MALLOC_SAMPLES.CHUNK,
                new GraphSampleValue(time, memStatistics.vMallocChunk));
        sampleValues.put(GraphSampleInserter.MALLOC_SAMPLES.USED,
                new GraphSampleValue(time, memStatistics.vMallocUsed));
        sampleValues.put(GraphSampleInserter.MALLOC_SAMPLES.TOTAL,
                new GraphSampleValue(time, memStatistics.vMallocTotal));
        List<server.websocket.models.GraphSampleData> insertedData = insertContextData(session, context, sampleValues);
        notifyNewData(context.getId(), insertedData);
    }

    public static final String INTERFACE_NAME = "eth0";

    public void insertData(Session session, NodeParseData nodeData)
    {
        Node node = findNode(session, nodeData.generalInfo.host);
        if (node == null)
            return;
        if (NodeStatusObservable.getInstance() != null)
            NodeStatusObservable.getInstance().setNodeOnline(node.getId());
        long timeMs = nodeData.generalInfo.encodeTimeS * 1000;
        insertAverage(session, node, nodeData.generalInfo, timeMs);
        insertTemperature(session, node, nodeData.generalInfo, timeMs);
        Optional<OverallInterfaceStatistics> optInterfaceStatistics = nodeData.overallInterfaceStatistics.stream()
                .filter(iStat -> iStat.interfaceName.equals(INTERFACE_NAME))
                .findFirst();
        if (optInterfaceStatistics.isPresent())
        {
            insertBytes(session, node, optInterfaceStatistics.get(), timeMs);
            insertPackets(session, node, optInterfaceStatistics.get(), timeMs);
        }
        insertMemStats(session, node, nodeData.memInfo, timeMs);
        insertMallocStats(session, node, nodeData.memInfo, timeMs);
    }

    private class GraphSampleValue {
        public GraphSampleValue(long timeMs, double value) {
            this.timeMs = timeMs;
            this.value = value;
        }

        public long timeMs;
        public double value;
    }
}
