package server.servlet.rest.rpi.readers;

import org.hibernate.Session;
import server.servlet.models.ClusterDescription;
import server.servlet.models.ClusterData;
import server.servlet.models.Node;

import java.util.*;

public class ClusterReader {
    public ClusterData readClusterDescription(Session session)
    {
        server.hibernate.models.ClusterDescription clusterDescription = (server.hibernate.models.ClusterDescription)
                session.createQuery("from ClusterDescription").getSingleResult();
        return formClusterData(session, clusterDescription);
    }

    private ClusterData formClusterData(Session session, server.hibernate.models.ClusterDescription hibernateDescription)
    {
        ClusterData clusterData = new ClusterData();
        org.hibernate.query.Query<server.hibernate.models.Node> nodeQuery =
                session.createQuery("from Node where clusterDescription.id = :clusterDescriptionId",
                        server.hibernate.models.Node.class);
        nodeQuery.setParameter("clusterDescriptionId", hibernateDescription.getId());
        List<server.hibernate.models.Node> nodes = nodeQuery.list();
        clusterData.setClusterDescription(formClusterDescription(hibernateDescription, nodes.size()));

        Map<Integer, Set<Long>> columnNodeMap = new HashMap<Integer, Set<Long>>();

        for (server.hibernate.models.Node node : nodes) {
            clusterData.getNodes().add(formNode(node, columnNodeMap));
        }

        clusterData.setCurrentTime(System.currentTimeMillis());
        return clusterData;
    }

    private Node formNode(server.hibernate.models.Node hibernateNode, Map<Integer, Set<Long>> columnNodeMapping)
    {
        Node node = new Node();
        node.setId(hibernateNode.getId());
        node.setIp(hibernateNode.getIp());
        node.setName(hibernateNode.getName());
        node.setRow(hibernateNode.getRowNumber());
        node.setColumn(hibernateNode.getColumnNumber());

        node.setOnline(hibernateNode.getIsOnline() == 1);
        if (!columnNodeMapping.containsKey(hibernateNode.getColumnNumber()))
            columnNodeMapping.put(hibernateNode.getColumnNumber(), new HashSet<Long>());
        columnNodeMapping.get(hibernateNode.getColumnNumber()).add(hibernateNode.getId());
        return node;
    }

    private ClusterDescription formClusterDescription(server.hibernate.models.ClusterDescription hibernateDescription,
                                                      int clusterNodesCount)
    {
        ClusterDescription clusterDescription = new ClusterDescription();
        clusterDescription.setClusterName(hibernateDescription.getName());
        clusterDescription.setConnectivity(hibernateDescription.getConnectivity());
        clusterDescription.setFQDN(hibernateDescription.getFqdn());
        clusterDescription.setHomogeneous(hibernateDescription.getIsHomogeneous() == 1? true : false);
        clusterDescription.setNumberOfNodes(clusterNodesCount);
        return clusterDescription;
    }


}
