package server.socket;
import org.hibernate.Session;
import server.contentObserver.NodeDataObservable;
import server.contentObserver.NodeStatusObservable;
import server.contentObserver.ParseResultInserter;
import server.hibernate.HibernateUtil;
import server.socket.servlet.NodeParseData;
import server.socket.servlet.SafeSocketListener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;

public class NodeServlet implements ServletContextListener {
    private SafeSocketListener socketListener;
    ParseResultInserter graphInserter;

        public void contextInitialized(ServletContextEvent event) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        NodeDataObservable.init();
        graphInserter = new ParseResultInserter(session);
        NodeStatusObservable.init(session);
        try {
            socketListener = new SafeSocketListener();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        session.close();
        socketListener.runLoop((nodeParseData)-> onParsedDataReceived(nodeParseData));
    }

    private void onParsedDataReceived(NodeParseData nodeParseData)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        graphInserter.insertData(session, nodeParseData);
        session.close();
    }

    public void contextDestroyed(ServletContextEvent event) {
        socketListener.destroy();
        NodeStatusObservable.destroy();

    }
}
