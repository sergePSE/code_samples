import dump.DumpFill;
import dump.TempDbDeployer;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.ServerSettings;
import server.ServerSettingsReader;
import server.dbDump.DbCleaner;
import server.dbDump.dbCheck.DbSizeGetter;
import server.hibernate.HibernateUtil;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Comparator;
import java.util.Optional;

public class DumpTest {

    private Session session;
    private StatelessSession statelessSession;
    TempDbDeployer tempDbDeployer;
    ServerSettings serverSettings = new ServerSettingsReader().readSettings();
    @Before
    public void createContext(){
        session = HibernateUtil.getSessionFactory().openSession();
        statelessSession = HibernateUtil.getSessionFactory().openStatelessSession();
        tempDbDeployer = new TempDbDeployer(session);
    }

    // as the output table contains rows of {node, time}, expected number can be calculated
    // better not to use this structure, but it saves computational power with one request
    private long getLinesToSave(Session session) {
        long recordCount = ((BigInteger)session.createNativeQuery(
        "select count(*) from (\n" +
                "select distinct Context.node_id, GraphSampleData.xValue from GraphSampleData\n" +
                "                inner join GraphSample on GraphSample.id = GraphSampleData.graphSample_id\n" +
                "                inner join Context on Context.id = GraphSample.context_id" +
            ") t").getSingleResult()).longValue();
        return recordCount;
    }

    private boolean isInDateBoundaries(Session session){
        long firstDate = (Long)session.createQuery("select MIN(xValue) from GraphSampleData")
                .getSingleResult();
        return System.currentTimeMillis() - Duration.ofDays(serverSettings.maxDbLogDays).toMillis() < firstDate;
    }

    private long getNewFileLineSize(String path) throws IOException {
        Optional<Path> lastFilePath = Files.list(Paths.get(path).toAbsolutePath()).filter(f -> !Files.isDirectory(f))
                .max(Comparator.comparingLong(f -> f.toFile().lastModified()));
        assert(lastFilePath.isPresent());
        FileReader reader = new FileReader(lastFilePath.get().toFile());
        int letter;
        long linesCount = 1;
        while ((letter = reader.read()) != -1){
            if ((char) letter == '\n')
                linesCount++;
        }
        return linesCount;
    }

    private void assertLines(long linesBefore) throws IOException {
        long fileLines = getNewFileLineSize(serverSettings.dumpPath);
        long eliminatedLines = linesBefore - getLinesToSave(session);
        // - 2:  header, last line is \n
        assert (fileLines - 2 == eliminatedLines);
    }

    @Test
    public void testDumpByDate() throws IOException {
        DumpFill dumpFill = new DumpFill();
        dumpFill.fillOutOfTimeBounds(session, tempDbDeployer.createGraphSample(statelessSession),
                serverSettings.maxDbLogDays);
        long beforeLines = getLinesToSave(session);
        StatelessSession statelessSession = HibernateUtil.getSessionFactory().openStatelessSession();
        new DbCleaner(serverSettings).run(statelessSession);
        assert (isInDateBoundaries(session));
        assertLines(beforeLines);

    }

    // test by size should be after test by date as it checks time boundaries
    @Test
    public void testDumpBySize() throws IOException, SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();

        DumpFill dumpFill = new DumpFill();
        dumpFill.fillSizeWithinTimeBounds(statelessSession, tempDbDeployer, serverSettings.maxDbLogDays,
                serverSettings.maxDbSizeMb);
        long beforeLines = getLinesToSave(session);
        new DbCleaner(serverSettings).run(statelessSession);

        assert(DbSizeGetter.getDbGraphSampleDataSizeMbAndCount(session).getFirst() < serverSettings.minDbSizeMb);
        assertLines(beforeLines);

    }

    @After
    public void cleanUp(){
        tempDbDeployer.cleanup(session);
        session.close();
        statelessSession.close();
        HibernateUtil.close();
    }
}
