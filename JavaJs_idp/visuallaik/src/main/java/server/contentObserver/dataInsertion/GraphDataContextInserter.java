package server.contentObserver.dataInsertion;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import server.hibernate.models.Context;
import server.hibernate.models.Graph;
import server.hibernate.models.Node;

import java.util.Arrays;
import java.util.Optional;

public class GraphDataContextInserter {
    public static Context getGraphSample(Session session, long nodeId, long graphId)
    {
        Query<Context> contextQuery = session.createQuery("from Context where node.id = :nodeId and " +
                "graph.id = :graphId");
        contextQuery.setParameter("nodeId", nodeId);
        contextQuery.setParameter("graphId", graphId);
        Optional<Context> foundContext = contextQuery.stream().findFirst();
        if (foundContext.isPresent())
            return foundContext.get();
        return null;
    }
    public static final String OVERVIEW_PLACE = "overview";
    public static final String PERFORMANCE_PLACE = "performance";
    public static final String TASK_PLACE = "task";

    public static String getDefaultPlace(String graphName)
    {
        String[] overviewGraphs = {GraphInserter.TEMPERATURE_NAME, GraphInserter.BYTES_NAME, GraphInserter.PACKETS_NAME};
        if (Arrays.stream(overviewGraphs).anyMatch(graph -> graph.equals(graphName)))
            return OVERVIEW_PLACE;
        String[] performanceGraphs = {GraphInserter.AVERAGE_NAME, GraphInserter.MALLOC_NAME, GraphInserter.MEMORY_NAME};
        if (Arrays.stream(performanceGraphs).anyMatch(graph -> graph.equals(graphName)))
            return PERFORMANCE_PLACE;
        return OVERVIEW_PLACE;
    }

    public static Context getOrUpdateGraphContext(Session session, Graph graph, Node node)
    {
        Context foundContext = getGraphSample(session, node.getId(), graph.getId());
        if (foundContext == null)
        {
            foundContext = new Context();
            foundContext.setGraph(graph);
            foundContext.setNode(node);
            foundContext.setPlace(getDefaultPlace(graph.getName()));
            foundContext.setCustomDataHeader(null);

            Transaction transaction = session.beginTransaction();
            session.save(foundContext);
            transaction.commit();
        }
        return foundContext;
    }
}
