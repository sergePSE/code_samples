package server.contentObserver.dataInsertion;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import server.hibernate.models.Context;
import server.hibernate.models.GraphSample;

import java.util.Optional;

public class GraphSampleInserter {
    public static class AVG_SAMPLES{
        public static String AVG_1MIN = "Average load for 1 minute";
        public static String AVG_5MIN = "Average load for 5 minute";
        public static String AVG_15MIN = "Average load for 15 minute";
    }

    public static class TEMPERATURE_SAMPLES{
        public static String TEMP = "";
    }

    public static class BYTES_SAMPLES{
        public static String BYTES_IN = "Received bytes";
        public static String BYTES_OUT = "Transmitted bytes";
    }

    public static class PACKETS_SAMPLES{
        public static String PACKETS_IN = "Received packets";
        public static String PACKETS_OUT = "Transmitted packets";
    }

    public static class MEMORY_SAMPLES{
        public static String MEM_TOTAL = "Total memory";
        public static String MEM_FREE = "Free memory";
        public static String MEM_AVAILABLE = "Available memory";
    }

    public static class MALLOC_SAMPLES{
        public static String TOTAL = "Vmalloc total";
        public static String USED = "Vmalloc used";
        public static String CHUNK = "Vmalloc chunk";
    }

    public static GraphSample getGraphSample(Session session, Context context, String sampleName)
    {
        Query<GraphSample> graphSampleQuery = session.createQuery("from GraphSample " +
                "where context.id = :contextId and name = :graphSampleName");
        graphSampleQuery.setParameter("contextId", context.getId());
        graphSampleQuery.setParameter("graphSampleName", sampleName);
        Optional<GraphSample> optFound = graphSampleQuery.stream().findFirst();
        if (optFound.isPresent())
            return optFound.get();
        return null;
    }

    private static GraphSample createDefault(Session session, Context context, String sampleName)
    {
        GraphSample graphSample = new GraphSample();
        graphSample.setName(sampleName);
        graphSample.setContext(context);
        Transaction transaction = session.beginTransaction();
        session.save(graphSample);
        transaction.commit();
        return graphSample;
    }

    public static GraphSample getOrUpdateGraphSample(Session session, Context context, String sampleName)
    {
        GraphSample graphSample = getGraphSample(session, context, sampleName);
        if (graphSample != null)
            return graphSample;
        return createDefault(session, context, sampleName);
    }
}
