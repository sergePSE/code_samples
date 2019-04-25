package server.hibernate.initialFill.databaseFill;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import server.hibernate.initialFill.staticData.NodeModel;
import server.hibernate.initialFill.staticData.NodeStaticDataModel;
import server.hibernate.models.ClusterDescription;
import server.hibernate.models.Node;

import java.util.List;
import java.util.Optional;

public class NodeFill {
    public void checkAndFill(Session session, ClusterDescription clusterDescription,
                             List<NodeModel> fileNodes)
    {
        for (NodeModel fileNode : fileNodes)
        {
            Node dbNode = getNode(session, clusterDescription.getId(), fileNode.getName());
            if (dbNode == null)
                createNode(session, clusterDescription, fileNode);
            else
                updateNode(session, fileNode, dbNode);
        }
    }

    private Node getNode(Session session, long clusterId, String nodeName)
    {
        Optional<Node> node = session.createQuery("from Node where name = :nodeName and " +
                "clusterDescription.id = :clusterId", Node.class)
            .setParameter("nodeName", nodeName)
            .setParameter("clusterId", clusterId)
            .getResultStream().findAny();
        return node.isPresent()? node.get() : null;
    }

    private Node createNode(Session session, ClusterDescription clusterDescription, NodeModel fileNode)
    {
        Node node = new Node();
        node.setClusterDescription(clusterDescription);
        node.setName(fileNode.getName());
        Transaction transaction = session.beginTransaction();
        Node nodeDb = fillNode(fileNode, node);
        session.save(nodeDb);
        transaction.commit();
        return nodeDb;
    }

    private Node updateNode(Session session, NodeModel fileNode, Node dbNode)
    {
        Transaction transaction = session.beginTransaction();
        dbNode = fillNode(fileNode, dbNode);
        session.update(dbNode);
        transaction.commit();
        return dbNode;
    }

    private Node fillNode(NodeModel fileNode, Node dbNode)
    {
        dbNode.setRowNumber(fileNode.getRow());
        dbNode.setColumnNumber(fileNode.getColumn());

        dbNode.setIp(fileNode.getIp());
        dbNode.setIsOnline(0);

        return dbNode;
    }

}
