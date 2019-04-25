import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;
import server.socket.servlet.NodeParseData;
import server.socket.servlet.SafeSocketListener;
import server.socket.servlet.SocketListener;
import socket.SocketSender;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SocketNodeListenerTest {

    private void sendMessages(List<String> messages) throws IOException {
        SocketSender sender = new SocketSender("localhost");
        sender.sendMessages(messages);
        sender.destroy();
    }

    private void sendAndAssertReceive(int messagesCount, SocketListener socketNodeListener)
            throws IOException {
        List<String> messages = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        for (int i = 0; i < messagesCount; i++) {
            NodeParseData parseData = NodeParseDataGenerator.getParseResult("wdw1");
            messages.add(objectMapper.writeValueAsString(parseData));
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    sendMessages(messages);
                } catch (IOException e) {
                    e.printStackTrace();
                    assert(false);

                }
            }
        }, 10);
        for (int i = 0; i < messagesCount; i++) {
            String data = socketNodeListener.getNextMessage();
            String expectedMessage = messages.get(i);
            assert (data.equals(expectedMessage));
        }
    }

    @Test
    public void testSocketReceiveMessage() throws IOException {
        SocketListener socketNodeListener = new SocketListener(SafeSocketListener.SOCKET_NUMBER);
        sendAndAssertReceive(1, socketNodeListener);
        sendAndAssertReceive(10, socketNodeListener);
        socketNodeListener.destroy();
    }
}
