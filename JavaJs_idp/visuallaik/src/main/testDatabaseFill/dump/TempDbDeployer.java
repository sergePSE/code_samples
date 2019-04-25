package dump;

import dbFill.utils.StringUtils;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import server.hibernate.models.*;

import java.util.List;
import java.util.stream.Stream;

public class TempDbDeployer {
    private boolean isClusterDescriptionCreated;
    public Context context;

    public TempDbDeployer(Session session) {
        Transaction transaction = session.beginTransaction();
        createContext(session);
        transaction.commit();
    }

    private ClusterDescription findOrCreateCreateClusterDescription(Session session) {
        List<ClusterDescription> roots = session.createQuery("from ClusterDescription").getResultList();
        if (!roots.isEmpty()){
            isClusterDescriptionCreated = false;
            return roots.get(0);
        }
        ClusterDescription clusterDescription = new ClusterDescription();
        clusterDescription.setName("cluster 1");
        session.save(clusterDescription);
        return clusterDescription;
    }

    private Node createNode(Session session, ClusterDescription clusterDescription){
        Node node = new Node();
        node.setName(StringUtils.generateRandomString(10));

        node.setRowNumber((Integer)session.createQuery("select max(rowNumber) from Node").getSingleResult() + 1);
        node.setColumnNumber((Integer)session.createQuery("select max(columnNumber) from Node")
                .getSingleResult() + 1);
        node.setClusterDescription(clusterDescription);
        session.save(node);
        return node;
    }

    private Graph createGraph(Session session) {
        Graph graph = new Graph();
        graph.setName(StringUtils.generateRandomString(10));
        session.save(graph);
        return graph;
    }


    private void createContext(Session session) {
        ClusterDescription clusterDescription = findOrCreateCreateClusterDescription(session);
        Node node = createNode(session, clusterDescription);
        Graph graph = createGraph(session);
        context = new Context();
        context.setNode(node);
        context.setGraph(graph);
        context.setPlace("overview");
        session.save(context);
    }

    public GraphSample createGraphSample(StatelessSession session) {
        GraphSample graphSample = new GraphSample();
        graphSample.setContext(context);
        graphSample.setName(StringUtils.generateRandomString(20));
        session.insert(graphSample);
        return graphSample;
    }

    public void cleanup(Session session){
        Transaction transaction = session.beginTransaction();
        Stream<GraphSample> samples = session.createQuery("from GraphSample where context.id = :contextId")
                .setParameter("contextId", context.getId())
                .getResultStream();
        samples.forEach(graphSample -> {
            session.createQuery("delete from GraphSampleData where graphSample.id = :graphSampleId")
                .setParameter("graphSampleId", graphSample.getId())
                .executeUpdate();
            session.delete(graphSample);
        });

        session.createQuery("delete from GraphSample where context.id=:contextId")
                .setParameter("contextId", context.getId())
                .executeUpdate();
        ClusterDescription clusterDescription = context.getNode().getClusterDescription();
        Node node = context.getNode();
        Graph graph = context.getGraph();
        session.delete(context);
        session.delete(graph);
        session.delete(node);
        if (isClusterDescriptionCreated){
            session.delete(clusterDescription);
            isClusterDescriptionCreated = false;
        }
        context = null;
        transaction.commit();
    }
}
