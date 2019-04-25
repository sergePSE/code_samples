package server.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import server.contentObserver.NodeDataObservable;
import server.websocket.models.GraphContextSubscription;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/websocket/graphContext")
public class GraphContextController {
    @OnOpen
    public void onOpen(Session session){
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        ObjectMapper objectMapper = new ObjectMapper();
        GraphContextSubscription subscription = null;
        try {
            subscription = objectMapper.readValue(message, GraphContextSubscription.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (NodeDataObservable.getInstance() != null)
            NodeDataObservable.getInstance().addSession(subscription.getContextId(), subscription.getPeriod(), session);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason){
        if (NodeDataObservable.getInstance() != null)
            NodeDataObservable.getInstance().removeSession(session);
    }
}
