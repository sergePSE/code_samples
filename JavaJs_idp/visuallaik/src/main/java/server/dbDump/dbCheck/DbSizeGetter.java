package server.dbDump.dbCheck;

import org.hibernate.query.QueryProducer;
import server.containers.Pair;

public class DbSizeGetter {
    private static final String GRAPHSAMPLEDATA_TABLE_SIZE_MB_REQUEST =
            "SELECT count(*) from GraphSampleData";

    // was checked from DB avg size (in fact 4 * 8 bytes)
    private static final int GRAPHSAMPLE_ENTYTIY_SIZE_BYTES = 33;
    public static Pair<Double, Long> getDbGraphSampleDataSizeMbAndCount(QueryProducer session) {
        long rowCount = (Long)session.createQuery(GRAPHSAMPLEDATA_TABLE_SIZE_MB_REQUEST).getSingleResult();
        double estimated_entity_size = rowCount * GRAPHSAMPLE_ENTYTIY_SIZE_BYTES / (1024.0*1024.0);
        return new Pair<>(estimated_entity_size, rowCount);
    }
}
