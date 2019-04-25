package dump;

import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import server.dbDump.dbCheck.DbSizeGetter;
import server.hibernate.HibernateUtil;
import server.hibernate.models.GraphSample;
import server.hibernate.models.GraphSampleData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;

public class DumpFill {
    private static final int RECORDS_PORTION_COUNT = 100000;

    public void fillSizeWithinTimeBounds(StatelessSession session, TempDbDeployer deployer,
                                         int logDays, int exceedDbSizeMb) throws SQLException {
        // don`t exceed time boundaries, so 0.9 can be used. still it's better to have more time range to use less
        // graphsample for test performance reasons
        long startTime = System.currentTimeMillis() - (long)(Duration.ofDays(logDays).toMillis() * 0.9);
        long endTime = System.currentTimeMillis();
        Connection connection = HibernateUtil.getOpenedConnection();
        PreparedStatement statement = connection.prepareStatement(
                "insert into GraphSampleData(xValue, yValue, graphSample_id) values (?, ?, ?)");
        while (DbSizeGetter.getDbGraphSampleDataSizeMbAndCount(session).getFirst() < exceedDbSizeMb) {
            int portion_used = 0;
            long currentTime = startTime;
            GraphSample currentGraphSample = deployer.createGraphSample(session);
            long graphSampleId = currentGraphSample.getId();
            while(portion_used++ < RECORDS_PORTION_COUNT) {
                fillData(statement, currentTime, graphSampleId);
                currentTime += 1000; // 1 sec
                if (currentTime > endTime){
                    currentTime = startTime;
                    currentGraphSample = deployer.createGraphSample(session);
                    graphSampleId = currentGraphSample.getId();
                }
            }
            statement.executeBatch();
            statement.clearBatch();
        }
        statement.close();
    }

    public void fillOutOfTimeBounds(Session session, GraphSample graphSample, int logDays){
        long nowTime = System.currentTimeMillis();

        Transaction transaction = session.beginTransaction();
        for(int i = 0; i < logDays * 2; i++){
            fillData(session, nowTime - Duration.ofDays(i).toMillis(), graphSample);
        }
        transaction.commit();
    }

    private GraphSampleData createData(long timeValue, GraphSample graphSample){
        GraphSampleData graphSampleData = new GraphSampleData();
        graphSampleData.setxValue(timeValue);
        graphSampleData.setyValue(Math.random()*100.0);
        graphSampleData.setGraphSample(graphSample);
        return graphSampleData;
    }

    private void fillData(Session session, long timeValue, GraphSample graphSample){
        session.save(createData(timeValue, graphSample));
    }

    private void fillData(PreparedStatement statement, long timeValue, long graphSampleId) throws SQLException {
        statement.setLong(1, timeValue);
        statement.setDouble(2, Math.random()*100);
        statement.setLong(3, graphSampleId);
        statement.addBatch();
    }
}
