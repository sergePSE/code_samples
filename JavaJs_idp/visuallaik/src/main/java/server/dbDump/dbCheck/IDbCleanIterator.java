package server.dbDump.dbCheck;

import org.hibernate.StatelessSession;
import server.containers.Pair;
import server.hibernate.models.GraphSampleData;

import java.util.List;

public interface IDbCleanIterator {
    List<GraphSampleData> getNext(StatelessSession session, int estimatedNumber);
    Pair<Long, Long> getTimeBoundaries();
}
