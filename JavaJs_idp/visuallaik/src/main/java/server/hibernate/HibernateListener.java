package server.hibernate;

import org.hibernate.SessionFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;

public class HibernateListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        // Just call the static initializer of that class
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try {
            // fill database with static data of cluster and database nodes
            new InitialHibernateDataFill().checkAndFillDatabase(sessionFactory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        HibernateUtil.getSessionFactory().close(); // Free all resources
    }
}