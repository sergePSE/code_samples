package dbFill;

import org.hibernate.Session;
import org.hibernate.Transaction;
import server.TimeParameters;
import server.hibernate.models.ClusterDescription;
import server.hibernate.models.CustomDataHeader;
import server.hibernate.models.Graph;
import server.hibernate.models.Node;

import java.util.*;

public class NodeFill {

    public Set<Node> fillWithRpiNodes(Session session, ClusterDescription clusterDescription, int rpiNodesCount,
                                  Set<Graph> graphs, Set<CustomDataHeader> dataHeaders, TimeParameters timeParameters)
    {
        ContextFill contextFill = new ContextFill();
        TaskFill taskFill = new TaskFill();

        Set<Node> nodes = createRpiNodes(rpiNodesCount, clusterDescription);
        int currentNodeIndex = 1;
        for (Node node : nodes) {
            Transaction transaction = session.beginTransaction();
            session.save(node);
            transaction.commit();
            taskFill.fillTasks(session, timeParameters, node);
            for (Graph graph : graphs) {
                contextFill.fillGraphContext(session, graph, node, timeParameters);
            }
            for (CustomDataHeader dataHeader: dataHeaders) {
                contextFill.fillCustomHeaderContext(session, dataHeader, node);
            }
            System.out.println(String.format("%d of %d nodes filled", currentNodeIndex++, nodes.size()));
        }
        return nodes;
    }

    private String generateIp()
    {
        List<String> byteStrs = new ArrayList<String>();
        int byteCount = 4;
        for(int i = 0; i < byteCount - 1; i++) {
            byteStrs.add(Integer.toString((int)(Math.random() * 255)));
        }
        byteStrs.add(Integer.toString((int)(1 + Math.random() * 254)));
        return String.join(".", byteStrs);
    }

    private final int NODES_PER_COLUMN = 5;
    private Set<Node> createRpiNodes(int count, ClusterDescription clusterDescription)
    {
        Set<Node> nodes = new HashSet<Node>();
        for (int i = 0; i < count; i++) {
            Node node = new Node();
            node.setIp(generateIp());
            node.setIsOnline(Math.random() > 0.5? 0 : 1);
            node.setName(String.format("wdw%d", i));
            node.setClusterDescription(clusterDescription);
            node.setColumnNumber(i / NODES_PER_COLUMN);
            node.setRowNumber(i % NODES_PER_COLUMN);
            nodes.add(node);
        }
        return nodes;
    }
}
