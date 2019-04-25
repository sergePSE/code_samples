package server.contentObserver.dataInsertion;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import server.hibernate.models.Graph;
import java.util.Optional;

public class GraphInserter {
    public static final String AVERAGE_NAME = "Average CPU load";
    public static final String TEMPERATURE_NAME = "Temperature";
    public static final String BYTES_NAME = "Total bytes";
    public static final String PACKETS_NAME = "Total packets";
    public static final String MEMORY_NAME = "Memory usage";
    public static final String MALLOC_NAME = "Allocated memory";

    private static Graph createGraph(String name, String yAxeName, String yAxeUnitName)
    {
        Graph graph = new Graph();
        graph.setName(name);
        graph.setyAxeName(yAxeName);
        graph.setyAxeUnitName(yAxeUnitName);
        return graph;
    }

    public static Graph getAverageGraphFrame()
    {
        return createGraph(AVERAGE_NAME, "average", "");
    }

    public static Graph getTemperatureGraphFrame()
    {
        return createGraph(TEMPERATURE_NAME, "temperature", "C");
    }

    public static Graph getBytesCountGraphFrame()
    {
        return createGraph(BYTES_NAME, "bytes", "b");
    }

    public static Graph getPacketsCountGraphFrame()
    {
        return createGraph(PACKETS_NAME, "packets", "");
    }


    public static Graph getMemoryGraphFrame()
    {
        return createGraph(MEMORY_NAME, "memory", "kB");
    }

    public static Graph getMallocGraphFrame()
    {
        return createGraph(MALLOC_NAME, "memory", "kB");
    }


    private static Graph getGraph(Session session, String graphName)
    {
        Query<Graph> query = session.createQuery("from Graph where name = :graphName");
        query.setParameter("graphName", graphName);
        Optional<Graph> graph = query.stream().findFirst();
        if (!graph.isPresent())
            return null;
        return graph.get();
    }

    private static Graph createGraph(Session session, Graph graph)
    {
        Transaction transaction = session.beginTransaction();
        session.save(graph);
        transaction.commit();
        return graph;
    }

    public static Graph getOrCreateGraph(Session session, Graph graphCandidate)
    {
        Graph graph = getGraph(session, graphCandidate.getName());
        if (graph != null)
            return graph;
        return createGraph(session, graphCandidate);
    }
}
