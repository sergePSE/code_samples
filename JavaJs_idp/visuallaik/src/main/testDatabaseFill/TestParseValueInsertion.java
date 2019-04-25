import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.query.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.mockito.ArgumentCaptor;
import server.StaticSettings;
import server.contentObserver.NodeDataObservable;
import server.contentObserver.NodeStatusObservable;
import server.contentObserver.ParseResultInserter;
import server.contentObserver.dataInsertion.GraphInserter;
import server.contentObserver.dataInsertion.GraphSampleInserter;
import server.hibernate.HibernateUtil;
import server.hibernate.models.Context;
import server.hibernate.models.GraphSample;
import server.hibernate.models.Node;
import server.socket.servlet.NodeParseData;
import server.socket.servlet.parse.general.GeneralInfo;
import server.socket.servlet.parse.meminfo.MemStatistics;
import server.socket.servlet.parse.netdev.DirectionalInterfaceStatistics;
import server.socket.servlet.parse.netdev.OverallInterfaceStatistics;
import server.websocket.models.ContextData;
import server.websocket.models.GraphSampleData;
import server.websocket.models.NodeStatusChange;

import javax.persistence.LockModeType;
import javax.transaction.Synchronization;
import javax.websocket.RemoteEndpoint;
import java.io.IOException;
import java.sql.Connection;
import java.time.Duration;
import java.util.*;

import static org.mockito.Mockito.*;

public class TestParseValueInsertion {
    private static final String NODE_NAME = "wdw1";

    private javax.websocket.Session getFakeSession()
            throws IOException {
        RemoteEndpoint.Basic basic = mock(RemoteEndpoint.Basic.class);
        javax.websocket.Session session = mock(javax.websocket.Session.class);
        when(session.getBasicRemote()).thenReturn(basic);
        return session;
    }

    public ParseResultInserter createInserter(Session session)
    {
        NodeDataObservable.init();
        ParseResultInserter graphInserter = new ParseResultInserter(session);
        NodeStatusObservable.init(session);

        return graphInserter;
    }

    void setNodesOnline(Session session)
    {
        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery("update Node set isOnline = 1");
        query.executeUpdate();
        transaction.commit();
    }

    void checkNodesOffline(Session session)
    {
        Query query = session.createQuery("from Node where isOnline = 1");
        assert (query.getResultList().size() == 0);
    }

    Node checkDbNodeStatus(Session session, String nodeName, boolean status)
    {
        Query<Node> query = session.createQuery("from Node where name=:nodeName")
                .setParameter("nodeName", nodeName);
        Node node = query.getSingleResult();
        // have no idea why node value is different from specific request of isOnline
        node.setIsOnline(((Query<Integer>)session.createQuery("select isOnline from Node where id=:nodeId")
                .setParameter("nodeId", node.getId())).getSingleResult());
        assert((node.getIsOnline() == 1) == status);
        return node;
    }

    void checkNodeStatusSocket(long nodeId, boolean status, javax.websocket.Session fakeSession) throws IOException {
        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);
        RemoteEndpoint.Basic basic = fakeSession.getBasicRemote();

        verify(basic, atLeastOnce()).sendText(statusCaptor.capture());
        String json = statusCaptor.getValue();
        ObjectMapper objectMapper = new ObjectMapper();
        NodeStatusChange nodeStatusChange = objectMapper.readValue(json, NodeStatusChange.class);
        assert(nodeStatusChange.getNodeId() == nodeId);
        assert(nodeStatusChange.isNodeStatus() == status);
    }


    // returns node as collateral object from database
    private Node testNodeChangeStatus(ParseResultInserter resultInserter) throws IOException, InterruptedException {
        NodeParseData parseData = NodeParseDataGenerator.getParseResult(NODE_NAME);
        javax.websocket.Session statusListener = getFakeSession();
        NodeStatusObservable.getInstance().subscribe(statusListener);
        Session session = sessionFactory.openSession();
        resultInserter.insertData(session, parseData);
        Node node = checkDbNodeStatus(session, NODE_NAME, true);
        checkNodeStatusSocket(node.getId(), true, statusListener);
        Thread.sleep((NodeStatusObservable.NODE_TIMEOUT_SEC + 1)*1000);
        checkDbNodeStatus(session, NODE_NAME, false);
        checkNodeStatusSocket(node.getId(), false, statusListener);

        session.close();
        return node;
    }

    private Context findContextAverageContext(Session session, long nodeId)
    {
        Query<Context> contextQuery = session.createQuery("from Context where node.id = :nodeId and " +
                "graph.name = :graphName");
        contextQuery.setParameter("nodeId", nodeId);
        contextQuery.setParameter("graphName", GraphInserter.AVERAGE_NAME);
        return contextQuery.getSingleResult();
    }

    private void assertAvgGraphSampleValue(String graphSampleName, double graphValue, GeneralInfo generalInfo)
    {
        if (graphSampleName.equals(GraphSampleInserter.AVG_SAMPLES.AVG_1MIN))
            assert(generalInfo.avg1Load == graphValue);
        else if (graphSampleName.equals(GraphSampleInserter.AVG_SAMPLES.AVG_5MIN))
            assert(generalInfo.avg5Load == graphValue);
        else if (graphSampleName.equals(GraphSampleInserter.AVG_SAMPLES.AVG_15MIN))
            assert(generalInfo.avg15Load == graphValue);
    }

    private void checkDbContextData(Session session, Context context, GeneralInfo generalInfo,
                                    Map<Long, String> graphSampleNameIdOut)
    {
        Query<GraphSample> graphSamplesQuery = session.createQuery("from GraphSample where context.id = :contextId");
        graphSamplesQuery.setParameter("contextId", context.getId());
        List<GraphSample> graphSamples = graphSamplesQuery.getResultList();
        for(GraphSample graphSample : graphSamples) {
            graphSampleNameIdOut.put(graphSample.getId(), graphSample.getName());
            Query<server.hibernate.models.GraphSampleData> lastDataQuery =
                    session.createQuery("from GraphSampleData " +
                            "where xValue = :timeMs and graphSample.id = :graphSampleId");
            lastDataQuery.setParameter("timeMs", generalInfo.encodeTimeS*1000);
            lastDataQuery.setParameter("graphSampleId", graphSample.getId());
            double graphValue = lastDataQuery.stream().findFirst().get().getyValue();
            assertAvgGraphSampleValue(graphSample.getName(), graphValue, generalInfo);
        }
    }

    private void checkSocketContextData(javax.websocket.Session fakeSession, GeneralInfo generalInfo,
                                        Map<Long, String> graphSamples)
            throws IOException {
        ArgumentCaptor<String> dataCaptor = ArgumentCaptor.forClass(String.class);
        RemoteEndpoint.Basic basic = fakeSession.getBasicRemote();
        verify(basic).sendText(dataCaptor.capture());
        String json = dataCaptor.getValue();

        ObjectMapper objectMapper = new ObjectMapper();
        ContextData nodeDataChange = objectMapper.readValue(json, ContextData.class);
        for (GraphSampleData graphSampleData: nodeDataChange.getGraphSampleData()) {
            assertAvgGraphSampleValue(graphSamples.get(graphSampleData.graphSampleId), graphSampleData.getyValue(),
                    generalInfo);
        }
    }


    private void testDataWrite(ParseResultInserter resultInserter, Node node) throws IOException {
        javax.websocket.Session dataSubscriberSession = getFakeSession();

        Session session = sessionFactory.openSession();
        // full check too long check, avg check is a good representative of parameters query
        Context avgContext = findContextAverageContext(session, node.getId());

        NodeDataObservable.getInstance()
                .addSession(avgContext.getId(), Duration.ofSeconds(1).toMillis(), dataSubscriberSession);
        NodeParseData parseData = NodeParseDataGenerator.getParseResult(NODE_NAME);
        resultInserter.insertData(session, parseData);
        Map<Long, String> avgGraphSampleIdNameMap = new HashMap<>();
        checkDbContextData(session, avgContext, parseData.generalInfo, avgGraphSampleIdNameMap);
        checkSocketContextData(dataSubscriberSession, parseData.generalInfo, avgGraphSampleIdNameMap);

        session.close();
    }

    private SessionFactory sessionFactory;

    @Before
    public void createIsolatedSessionFactory()
    {
        StaticSettings.databaseIsolationLevel = Connection.TRANSACTION_SERIALIZABLE;
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Test
    public void testParseValueInsertion() throws IOException, InterruptedException {
        Session session = sessionFactory.openSession();
        setNodesOnline(session);

        ParseResultInserter resultInserter = createInserter(session);
        checkNodesOffline(session);

        session.close();
        Node node = testNodeChangeStatus(resultInserter);
        // checks data content after all data context was created to mention websocket message
        testDataWrite(resultInserter, node);

        NodeStatusObservable.destroy();
    }

    @After
    public void closeIsolatedSessionFactory()
    {
        sessionFactory.close();
    }
}
