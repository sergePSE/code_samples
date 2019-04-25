package server.hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import server.hibernate.initialFill.databaseFill.ClusterDescriptionFill;
import server.hibernate.initialFill.databaseFill.NodeFill;
import server.hibernate.initialFill.staticData.FileDataLoader;
import server.hibernate.initialFill.staticData.NodeStaticDataModel;
import server.hibernate.models.ClusterDescription;

import java.io.IOException;

public class InitialHibernateDataFill {
    /*
    check if database has initial data, like cluster data, nodes
     */
    public void checkAndFillDatabase(SessionFactory sessionFactory) throws IOException {
        Session session = sessionFactory.openSession();

        FileDataLoader fileDataLoader = new FileDataLoader();
        NodeStaticDataModel nodeStaticDataModel = fileDataLoader.readData();
        ClusterDescription clusterDescription = new ClusterDescriptionFill().checkOrFillClusterDescription(session,
                nodeStaticDataModel.getClusterDescription());
        new NodeFill().checkAndFill(session, clusterDescription, nodeStaticDataModel.getNodes());
        session.close();

    }
}
