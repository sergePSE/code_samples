package dbFill;

import org.hibernate.Session;
import org.hibernate.Transaction;
import server.hibernate.models.Graph;
import dbFill.utils.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class GraphFill {
    public Set<Graph> fillWithGraph(Session session, int count)
    {
        Set<Graph> graphs = new HashSet<Graph>();
        for (int i = 0; i < count; i++) {
            Transaction transaction = session.beginTransaction();
            Graph graph = createGraph();
            session.save(graph);
            transaction.commit();
            graphs.add(graph);
        }
        return graphs;
    }

    private static final int NAME_LENGTH = 20;
    private static final int AXE_NAME_LENGTH = 10;
    private static final int AXE_UNIT_NAME_LENGTH = 10;


    private Graph createGraph()
    {
        Graph graph = new Graph();
        graph.setName(StringUtils.generateRandomString(NAME_LENGTH));
        graph.setyAxeName(StringUtils.generateRandomString(AXE_NAME_LENGTH));
        graph.setyAxeUnitName(StringUtils.generateRandomString(AXE_UNIT_NAME_LENGTH));
        return graph;
    }

}
