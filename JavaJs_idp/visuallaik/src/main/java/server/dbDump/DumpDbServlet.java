package server.dbDump;

import server.ServerSettings;
import server.ServerSettingsReader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DumpDbServlet implements ServletContextListener {
    DumpDbServletInstance dumpDbServletInstance;

    public void contextInitialized(ServletContextEvent event) {
        ServerSettings serverSettings = new ServerSettingsReader().readSettings();
        dumpDbServletInstance = new DumpDbServletInstance(serverSettings);
    }

    public void contextDestroyed(ServletContextEvent event) {
        dumpDbServletInstance.destroy();
    }
}
