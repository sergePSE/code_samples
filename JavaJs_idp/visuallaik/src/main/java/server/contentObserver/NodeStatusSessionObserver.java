package server.contentObserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import server.websocket.models.NodeStatusChange;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class NodeStatusSessionObserver implements Observer {
    public NodeStatusSessionObserver(Session webSession) {
        this.webSession = webSession;
    }

    public Session webSession;

    @Override
    public void update(Observable o, Object arg) {
        ObjectMapper objectMapper = new ObjectMapper();
        NodeStatusChange statusChange = (NodeStatusChange)arg;
        try {
            webSession.getBasicRemote().sendText(objectMapper.writeValueAsString(statusChange));
        } catch (IOException e) {
            e.printStackTrace();
            NodeStatusObservable.getInstance().unsubscribe(webSession);
        }
    }
}
