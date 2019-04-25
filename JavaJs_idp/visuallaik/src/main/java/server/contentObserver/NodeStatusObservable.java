package server.contentObserver;

import org.hibernate.Session;
import org.hibernate.Transaction;
import server.hibernate.HibernateUtil;
import server.hibernate.models.Node;
import server.websocket.models.NodeStatusChange;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class NodeStatusObservable extends Observable {

    private static NodeStatusObservable instance;

    public static NodeStatusObservable init(Session session)
    {
        destroy();
        instance = new NodeStatusObservable(session);
        return instance;
    }

    public static NodeStatusObservable getInstance()
    {
        return instance;
    }

    private static final long TIMER_FREQUENCY_MSEC = 1000;
    private NodeStatusObservable(Session session) {
        setAllNodesOffline(session);

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                setNodeExpiredOffline();
            }
        }, NODE_TIMEOUT_SEC * 1000, TIMER_FREQUENCY_MSEC);
    }
    private ConcurrentHashMap<Long, Calendar> activeNodeTimeMap = new ConcurrentHashMap<>();
    HashMap<javax.websocket.Session, NodeStatusSessionObserver> sessions = new HashMap<>();
    private Timer timer;

    private void setDbNodeState(Session session, List<Long> nodeIds, boolean isOnline)
    {
        Transaction transaction = session.beginTransaction();
        for (long nodeId : nodeIds) {
            Node node = session.get(Node.class, nodeId);
            node.setIsOnline(isOnline? 1 : 0);
            session.update(node);
        }
        transaction.commit();
    }

    private void setNodeExpiredOffline()
    {
        Calendar now = Calendar.getInstance();
        List<Long> expiredNodes = activeNodeTimeMap.entrySet().stream().
                filter(node -> node.getValue().before(now))
                .map(node -> node.getKey()).collect(Collectors.toList());
        if (expiredNodes.size() == 0)
            return;
        Session session = HibernateUtil.getSessionFactory().openSession();
        expiredNodes.forEach(nodeId -> {
            notifySubsriptors(nodeId, false);
            activeNodeTimeMap.remove(nodeId);
        });
        setDbNodeState(session, expiredNodes, false);
        session.close();
    }

    private void setAllNodesOffline(Session session)
    {
        Transaction transaction = session.beginTransaction();
        List<Node> nodes = session.createQuery("from Node").getResultList();
        for (Node node: nodes) {
            node.setIsOnline(0);
            session.update(node);
        }
        transaction.commit();
    }

    public static final int NODE_TIMEOUT_SEC = 10;

    public void setNodeOnline(long nodeId)
    {
        Calendar expireDate = Calendar.getInstance();
        expireDate.add(Calendar.SECOND, NODE_TIMEOUT_SEC);
        Calendar previousExpireDate = activeNodeTimeMap.put(nodeId, expireDate);

        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Long> ids = new ArrayList<>();
        ids.add(nodeId);
        setDbNodeState(session, ids, true);
        session.close();
        if (previousExpireDate == null) {
            notifySubsriptors(nodeId, true);
        }
    }

    private void notifySubsriptors(long nodeId, boolean status)
    {
        NodeStatusChange statusChange = new NodeStatusChange(nodeId, status);
        this.setChanged();
        this.notifyObservers(statusChange);
    }

    public void subscribe(javax.websocket.Session webSession)
    {
        NodeStatusSessionObserver observer = new NodeStatusSessionObserver(webSession);
        sessions.put(webSession, observer);
        this.addObserver(observer);
    }

    public void unsubscribe(javax.websocket.Session webSession)
    {
        NodeStatusSessionObserver observer = sessions.get(webSession);
        if (observer == null)
            return;
        sessions.remove(webSession);
        this.deleteObserver(observer);
    }

    public static void destroy()
    {
        if (instance == null)
            return;
        instance.timer.cancel();
        instance.timer.purge();
        instance = null;
    }
}
