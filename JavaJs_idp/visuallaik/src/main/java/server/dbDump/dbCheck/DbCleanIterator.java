package server.dbDump.dbCheck;

import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import server.containers.Pair;
import server.hibernate.models.GraphSampleData;

import java.util.ArrayList;
import java.util.List;

public class DbCleanIterator implements IDbCleanIterator {
    private long bottomDate;
    private long upperDate;

    public DbCleanIterator(long bottomDate, long upperDate) {
        this.bottomDate = bottomDate;
        this.upperDate = upperDate;
    }

    private void deleteData(StatelessSession session, List<GraphSampleData> orderedGraphData) {
        long lastTime = orderedGraphData.get(orderedGraphData.size() - 1).getxValue();
        List<GraphSampleData> lastDateData = new ArrayList<>();
        for (int i = orderedGraphData.size() - 1; i >= 0 ; i--) {
            if (orderedGraphData.get(i).getxValue() < lastTime)
                break;
            lastDateData.add(orderedGraphData.get(i));
        }
        Transaction transaction = session.beginTransaction();
        session.createQuery("delete from GraphSampleData where xValue < :lastTime")
                .setParameter("lastTime", lastTime)
                .executeUpdate();
        for (GraphSampleData graphSampleData : lastDateData) {
            session.delete(graphSampleData);
        }
        transaction.commit();
    }
    // required to write to file complete lines of data, not separating one possible line on two
    private void filterNotCompletedTimeTail(List<GraphSampleData> fullSortedData, int estimatedNumber){
        // selection is already complete, as queried number is less then it can be
        if (fullSortedData.size() < estimatedNumber)
            return;
        long lastTime = fullSortedData.get(fullSortedData.size() - 1).getxValue();
        // last time is in end as it's sorted
        for (int i = fullSortedData.size() - 1; i >= 0; i--) {
            if (fullSortedData.get(i).getxValue() != lastTime)
                break;
            fullSortedData.remove(i);
        }
    }

    @Override
    public List<GraphSampleData> getNext(StatelessSession session, int estimatedNumber) {
        List<GraphSampleData> data = session.createQuery("from GraphSampleData where xValue <= :upperDate " +
                "order by xValue ASC ")
            .setParameter("upperDate", upperDate)
            .setMaxResults(estimatedNumber)
            .getResultList();
        if (data.isEmpty())
            return data;
        filterNotCompletedTimeTail(data, estimatedNumber);
        deleteData(session, data);
        return data;
    }

    @Override
    public Pair<Long, Long> getTimeBoundaries() {
        return new Pair<>(bottomDate, upperDate);
    }
}
