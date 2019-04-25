package server.hibernate.initialFill.databaseFill;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import server.hibernate.models.ClusterDescription;

import java.util.Optional;

public class ClusterDescriptionFill {
    public ClusterDescription checkOrFillClusterDescription(Session session,
        server.hibernate.initialFill.staticData.ClusterDescription fileClusterData)
    {
        Query<ClusterDescription> query = session.createQuery("from ClusterDescription", ClusterDescription.class);
        Optional<ClusterDescription> clusterDescription = query.stream().findFirst();
        if (clusterDescription.isPresent())
            return clusterDescription.get();
        return fillClusterDescription(session, fileClusterData);
    }

    private ClusterDescription fillClusterDescription(Session session,
                                       server.hibernate.initialFill.staticData.ClusterDescription fileClusterData)
    {
        ClusterDescription clusterDescription = new ClusterDescription();
        clusterDescription.setName(fileClusterData.getName());
        clusterDescription.setFqdn(fileClusterData.getFqdn());
        clusterDescription.setIsHomogeneous(fileClusterData.getIsHomogeneous());
        clusterDescription.setConnectivity(fileClusterData.getConnectivity());
        Transaction transaction = session.beginTransaction();
        session.save(clusterDescription);
        transaction.commit();
        return clusterDescription;
    }
}
