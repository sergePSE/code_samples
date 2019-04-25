import dbFill.CustomDataHeaderFill;
import dbFill.GraphFill;
import dbFill.NodeFill;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joda.time.*;
import server.hibernate.HibernateUtil;
import server.hibernate.models.*;
import server.TimeParameters;

import java.util.Set;


/*
not actually a test, but a test class to create a new test data. Generates a test data for the parameters, mentioned in
static values
 */
class TestDatabaseFill {
    // 1 month
    public static Duration GENERATION_TIME = Duration.standardDays(1);
    public static int RPI_NODES_COUNT = 8;
    public static int GRAPH_COUNT = 6;
    public static int CUSTOM_HEADER_COUNT = 5;

    public static void main(String[] args) throws InterruptedException {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        ClusterDescription clusterDescription = fillClusterDescription(session);
        Set<Graph> graphs = (new GraphFill()).fillWithGraph(session, GRAPH_COUNT);
        Set<CustomDataHeader> customDataHeaders = (new CustomDataHeaderFill()).fillWithCustomDataHeader(session,
                CUSTOM_HEADER_COUNT);
        Set<Node> nodes = (new NodeFill()).fillWithRpiNodes(session, clusterDescription, RPI_NODES_COUNT, graphs,
                customDataHeaders, createTimeParameters());
        session.close();
        HibernateUtil.close();
        return;
    }

    private static ClusterDescription fillClusterDescription(Session session)
    {
        Transaction transaction = session.beginTransaction();
        ClusterDescription clusterDescription = new ClusterDescription();
        clusterDescription.setName("HimMUC");
        clusterDescription.setConnectivity("Ethernet");
        clusterDescription.setIsHomogeneous(1);
        clusterDescription.setFqdn("fn.lrr.in.tum.de");
        session.save(clusterDescription);
        transaction.commit();
        return clusterDescription;
    }

    private static TimeParameters createTimeParameters()
    {
        TimeParameters timeParameters = new TimeParameters();
        DateTime nowDateTime = DateTime.now();
        timeParameters.endDate = nowDateTime.getMillis();
        timeParameters.startDate = nowDateTime.minus(GENERATION_TIME).getMillis();
        timeParameters.durationStep = Duration.standardSeconds(1).getMillis();
        return timeParameters;
    }







}
