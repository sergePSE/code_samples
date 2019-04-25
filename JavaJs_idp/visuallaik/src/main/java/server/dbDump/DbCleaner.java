package server.dbDump;

import org.hibernate.StatelessSession;
import server.ServerSettings;
import server.containers.Pair;
import server.dbDump.dbCheck.DateDbCheck;
import server.dbDump.dbCheck.IDbCheck;
import server.dbDump.dbCheck.IDbCleanIterator;
import server.dbDump.dbCheck.SizeDbCheck;
import server.hibernate.models.GraphSampleData;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class DbCleaner {
    private ServerSettings serverSettings;

    public DbCleaner(ServerSettings serverSettings) {
        this.serverSettings = serverSettings;
    }

    private class IteratorResponse {
        public IteratorResponse(boolean isSizeLimitted, IDbCleanIterator cleanIterator) {
            this.isSizeLimitted = isSizeLimitted;
            this.cleanIterator = cleanIterator;
        }

        private boolean isSizeLimitted;
        private IDbCleanIterator cleanIterator;
    }

    private IteratorResponse getCleanIterator(StatelessSession session) {
        IDbCheck checker = new DateDbCheck(serverSettings.minDbLogDays, serverSettings.maxDbLogDays);
        IDbCleanIterator cleanIterator = checker.isLimitReached(session);
        if (cleanIterator != null)
            return new IteratorResponse(false, cleanIterator);
        checker = new SizeDbCheck(serverSettings.minDbSizeMb, serverSettings.maxDbSizeMb);
        cleanIterator = checker.isLimitReached(session);
        if (cleanIterator != null)
            return new IteratorResponse(true, cleanIterator);
        return null;
    }

    private static Calendar getCalendar(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return calendar;
    }

    private final static int RECORDS_PORTION_COUNT = 100000;
    private static Logger logger = Logger.getLogger("DbCleaner");

    public void run(StatelessSession session) {

        IteratorResponse iteratorResponse = getCleanIterator(session);
        if (iteratorResponse == null)
            return;

        HashMap<Long, DbGraphSampleTranscript> headers = new DbDataCollector().getHeaders(session);
        GraphDataTable graphDataTable = new GraphDataTable(headers);

        Pair<Long, Long> cleanTimeBounds = iteratorResponse.cleanIterator.getTimeBoundaries();
        CsvWriter csvWriter = null;
        try {
            csvWriter = new CsvWriter(serverSettings.dumpPath, iteratorResponse.isSizeLimitted,
                    getCalendar(cleanTimeBounds.getFirst()), getCalendar(cleanTimeBounds.getSecond()));
            csvWriter.writeLineToFile(graphDataTable.getHeaders(), true);
            List<GraphSampleData> graphSampleData =
                    iteratorResponse.cleanIterator.getNext(session, RECORDS_PORTION_COUNT);
            while (!graphSampleData.isEmpty()) {
                graphSampleData.forEach(sampleRecord -> graphDataTable.addData(sampleRecord.getGraphSample().getId(),
                        sampleRecord.getxValue(), sampleRecord.getyValue()));
                Collection<Collection<String>> tableLines = graphDataTable.emptyTable();
                csvWriter.writeLinesToFile(tableLines, false);
                graphSampleData = iteratorResponse.cleanIterator.getNext(session, RECORDS_PORTION_COUNT);
            }
        } catch (IOException e) {
            logger.warning("Exception" + e.toString());
            e.printStackTrace();
            if (csvWriter != null)
                csvWriter.destroy();
            session.close();
            return;
        }

        csvWriter.destroy();
    }
}
