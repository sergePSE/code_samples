package server.contentObserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import server.websocket.models.ContextData;
import server.websocket.models.GraphSampleData;

import javax.websocket.Session;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class NodeDataObservable {

    private final static int ENTRY_TIMEOUT_MIN = 30;
    private static NodeDataObservable instance;
    public static NodeDataObservable init() {
        instance = new NodeDataObservable();
        return instance;
    }

    public static NodeDataObservable getInstance()
    {
        return instance;
    }

    private NodeDataObservable() {
    }

    private class WebSocketSessionSubscription {
        public WebSocketSessionSubscription(long contextId, long period) {
            this.contextId = contextId;
            this.addDate = Calendar.getInstance();
            this.period = period;
            updateTimer();
        }

        public void updateTimer(){
            nextUpdateTime = System.currentTimeMillis() + period;
        }

        public long contextId;
        public Calendar addDate;
        public long nextUpdateTime;
        private long period;
    }

    private ArrayMap<Long, Session> contextSessionSubscribers = new ArrayMap<>();
    private Map<Session, WebSocketSessionSubscription> sessionSubscriptions = new ConcurrentHashMap<>();

    private void cleanOldSessions()
    {
        Calendar lastActualDate = Calendar.getInstance();
        lastActualDate.add(Calendar.MINUTE, -ENTRY_TIMEOUT_MIN);

        List<Session> oldSessions = sessionSubscriptions.entrySet().stream()
                .filter(sessionEntry -> sessionEntry.getValue().addDate.before(lastActualDate))
                .map(sessionEntry -> sessionEntry.getKey())
                .collect(Collectors.toList());
        for (Session oldSession : oldSessions)
                removeSession(oldSession);
    }

    public void addSession(long contextId, long period, Session session)
    {
        contextSessionSubscribers.add(contextId, session);
        sessionSubscriptions.put(session, new WebSocketSessionSubscription(contextId, period));
        cleanOldSessions();
    }

    public void removeSession(Session session)
    {
        if (!sessionSubscriptions.containsKey(session))
            return;
        WebSocketSessionSubscription subscription = sessionSubscriptions.remove(session);
        contextSessionSubscribers.remove(subscription.contextId, session);
    }

    public void notifyData(long contextId, List<GraphSampleData> sampleValue)
    {
        List<Session> subscribedSessions = contextSessionSubscribers.getValues(contextId);
        if (subscribedSessions == null)
            return;
        ObjectMapper objectMapper = new ObjectMapper();
        ContextData contextData = new ContextData();
        contextData.setContextId(contextId);
        contextData.setGraphSampleData(sampleValue);
        long currentTime = System.currentTimeMillis();
        try {
            String contextDataString = objectMapper.writeValueAsString(contextData);
            for (Session session: subscribedSessions) {

                WebSocketSessionSubscription subscription = sessionSubscriptions.get(session);
                if (subscription.nextUpdateTime > currentTime)
                    continue;
                try {
                    session.getBasicRemote().sendText(contextDataString);
                    subscription.updateTimer();
                } catch (IOException e) {
                    removeSession(session);
                    e.printStackTrace();
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }
    }

}
