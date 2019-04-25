package server.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import server.StaticSettings;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    private static Connection openedConnection;

    static {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            Configuration configuration = new Configuration().configure();
            if (StaticSettings.databaseIsolationLevel != 0)
                configuration.setProperty("hibernate.connection.isolation",
                        String.valueOf(StaticSettings.databaseIsolationLevel));
            sessionFactory = configuration.buildSessionFactory();

        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Connection getOpenedConnection() throws SQLException {
        if (openedConnection == null)
        {
            openedConnection = sessionFactory
                    .getSessionFactoryOptions().getServiceRegistry()
                    .getService(ConnectionProvider.class).getConnection();
        }
        return openedConnection;
    }

    public static void executeRawRequest(String request) throws SQLException {
        Statement statement = getOpenedConnection().createStatement();
        statement.execute(request);
        statement.close();
    }

    public static void close()
    {
        try {
            sessionFactory
                    .getSessionFactoryOptions().getServiceRegistry()
                    .getService(ConnectionProvider.class).closeConnection(openedConnection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sessionFactory.close();
    }


}