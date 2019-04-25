package server.dbDump.dbCheck;

import org.hibernate.StatelessSession;
import org.hibernate.query.Query;
import org.hibernate.query.QueryProducer;
import server.containers.Pair;

import java.math.BigInteger;

public class SizeDbCheck implements IDbCheck {
    private double minSizeMb;
    private double maxSizeMb;

    public SizeDbCheck(double minSizeMb, double maxSizeMb) {
        this.minSizeMb = minSizeMb;
        this.maxSizeMb = maxSizeMb;
    }

    private long getRecordsNumberForDate(QueryProducer session, long date) {
        return (Long)session.createQuery("select count(*) from GraphSampleData where xValue <= :date")
                .setParameter("date", date)
                .getSingleResult();
    }

    Pair<Long, Long> getMinMaxDate(QueryProducer session){
        Query query = session.createQuery("select min(xValue), max(xValue) from GraphSampleData");
        Object[] size = (Object[])query.getSingleResult();
        return new Pair<>((Long)size[0], (Long)size[1]);

    }

    private class DateRange {
        public DateRange(long lowDateBound, long upperDateBound, long nodesCount) {
            this.lowDateBound = lowDateBound;
            this.upperDateBound = upperDateBound;
            this.nodesCount = nodesCount;
        }

        public long lowDateBound;
        public long upperDateBound;
        public long nodesCount;
    }

    // finds last limit date with algorithm of binary search
    // binary search is not actual if it's possible to find it with LIMIT request, which plays a role of ROW_NUMBER
    private long getLastLimitDate(QueryProducer session, long deleteMinRecordsCountRequired){
        Long lastTime = ((BigInteger)session.createNativeQuery("select xValue from GraphSampleData " +
                "order by xValue ASC LIMIT :recordsCount,1")
                .setParameter("recordsCount", deleteMinRecordsCountRequired)
                .getSingleResult()).longValue();
        return lastTime;
        // binary search works for all dbs, but necessary only for mysql
//        DateRange range = new DateRange(minMaxDate.getKey(), minMaxDate.getValue(), totalRecordsCount);
//        long lastRecordsCount = totalRecordsCount;
//        long previousRecordsCount;
//        do {
//            previousRecordsCount = lastRecordsCount;
//            long halfBound = (range.lowDateBound + range.upperDateBound) / 2;
//            lastRecordsCount = getRecordsNumberForDate(session, halfBound);
//            if (lastRecordsCount < deleteMinRecordsCountRequired)
//                range.lowDateBound = (range.upperDateBound + range.lowDateBound) / 2;
//            else {
//                range.upperDateBound = (range.upperDateBound + range.lowDateBound) / 2;
//                range.nodesCount = lastRecordsCount;
//            }
//
//        } while (lastRecordsCount != previousRecordsCount);
//        return range.upperDateBound;
    }


    @Override
    public IDbCleanIterator isLimitReached(StatelessSession session) {
        Pair<Double, Long> tableSize = DbSizeGetter.getDbGraphSampleDataSizeMbAndCount(session);
        if (tableSize == null)
            return null;
        if (tableSize.getFirst() < maxSizeMb)
            return null;
        // assume that required size can be emptied proportionally to the records number (in fact not, because of the
        // index size)
        double mbToDelete = tableSize.getFirst() - minSizeMb;
        if (mbToDelete > tableSize.getFirst()) {
            mbToDelete = tableSize.getFirst();
        }
        long recordsToDelete = (long)((tableSize.getSecond() * mbToDelete) / tableSize.getFirst());
        Pair<Long, Long> minMaxDate = getMinMaxDate(session);
        long lastLimitDate = getLastLimitDate(session, recordsToDelete);
        return new DbCleanIterator(minMaxDate.getFirst(), lastLimitDate);
    }
}
