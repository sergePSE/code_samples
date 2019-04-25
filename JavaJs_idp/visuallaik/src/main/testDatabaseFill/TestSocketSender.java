import server.contentObserver.NodeStatusObservable;
import socket.SocketSender;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestSocketSender {

    private static final long PERIOD_MS = 1000;
    private static final long DURATION_MS = 1000 * 60 * 10;
    private static final long IRREGULAR_NODE_PERIOD = NodeStatusObservable.NODE_TIMEOUT_SEC * 2 * 1000L;
    private static final String REGULAR_NODE_NAME = "wdw1";
    private static final String IRREGULAR_NODE_NAME = "wdw2";

    private static void sendNodeParseData(SocketSender sender, Map<String, Long> sentMessages,
                                          Long nowTime, String nodeName) throws IOException {
        String message = NodeMessageGenerator.generateMessage(nodeName);
        sender.sendMessages(Arrays.asList(message));
        System.out.print(String.format("%d: Message was sent on %s\n", System.currentTimeMillis(), nodeName));
        sentMessages.put(nodeName, nowTime);
    }

    // send every period message to one node, and send a message to other node every 10 seconds to test switching status
    // result
    private static void sendData(Map<String, Long> lastMessageTime, Long nowTime)
            throws IOException {
        SocketSender sender = new SocketSender("localhost");
        sendNodeParseData(sender, lastMessageTime, nowTime, REGULAR_NODE_NAME);
        Long lastSentIrregularNodeMessageTime = lastMessageTime.containsKey(IRREGULAR_NODE_NAME)?
                lastMessageTime.get(IRREGULAR_NODE_NAME) : 0L;
        if (nowTime - lastSentIrregularNodeMessageTime > IRREGULAR_NODE_PERIOD)
            sendNodeParseData(sender, lastMessageTime, nowTime, IRREGULAR_NODE_NAME);
        sender.destroy();
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        Map<String, Long> lastSentMessages = new HashMap<>();

        for (long i = 0; i < DURATION_MS; i+= PERIOD_MS) {
            Thread.sleep(PERIOD_MS);
            sendData(lastSentMessages, i);
        }
    }
}
