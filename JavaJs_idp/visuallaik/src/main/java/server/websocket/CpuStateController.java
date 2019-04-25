package server.websocket;

import server.contentObserver.NodeStatusObservable;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/websocket/rpiState")
public class CpuStateController {
    @OnOpen
    public void onOpen(Session session){
        if (NodeStatusObservable.getInstance() != null)
            NodeStatusObservable.getInstance().subscribe(session);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason){
        if (NodeStatusObservable.getInstance() != null)
            NodeStatusObservable.getInstance().unsubscribe(session);
    }
}
