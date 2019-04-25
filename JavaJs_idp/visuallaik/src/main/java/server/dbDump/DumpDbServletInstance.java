package server.dbDump;

import org.hibernate.Session;
import org.hibernate.StatelessSession;
import server.ServerSettings;
import server.ServerSettingsReader;
import server.hibernate.HibernateUtil;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Timer;
import java.util.TimerTask;

public class DumpDbServletInstance {
    private ServerSettings serverSettings;
    private Timer checker = new Timer();

    private static final int CHECK_INTERVAL_HOURS = 1;
    private static final int PLANNED_STARTUP_MINUTES = 1;
    private static boolean isRunning = false;

    public DumpDbServletInstance(ServerSettings serverSettings) {
        this.serverSettings = new ServerSettingsReader().readSettings();
        checker.schedule(new TimerTask() {
            @Override
            public void run() {
                StatelessSession session = HibernateUtil.getSessionFactory().openStatelessSession();
                try {
                    if (isRunning)
                        return;
                    isRunning = true;
                    DbCleaner dbCleaner = new DbCleaner(serverSettings);
                    dbCleaner.run(session);
                    isRunning = false;
                } catch (Exception e) {
                    isRunning = false;
                    e.printStackTrace();
                }
                finally {
                    session.close();
                }
            }
        }, Duration.ofMinutes(PLANNED_STARTUP_MINUTES).toMillis(), Duration.ofHours(CHECK_INTERVAL_HOURS).toMillis());
    }


    public void destroy() {
        checker.cancel();
        this.checker = null;
    }
}
