package server.socket.servlet;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Consumer;

public class SafeSocketListener {
    public static final int SOCKET_NUMBER = 5455;
    private SocketListener nodeListener;

    private final long MIN_REST_TIME_MS = 10;
    private final long MAX_REST_TIME_MS = Duration.ofDays(1).toMillis();
    private final double REST_TIME_STEP_MULTIPLIER = 2.0;
    private SlidingWindow slidingWindow = new SlidingWindow(MIN_REST_TIME_MS, MIN_REST_TIME_MS, MAX_REST_TIME_MS,
            REST_TIME_STEP_MULTIPLIER);
    private boolean isEnded;


    public SafeSocketListener() throws IOException {
        this.nodeListener = new SocketListener(SOCKET_NUMBER);
        isEnded = false;
    }

    public void runLoop(Consumer<NodeParseData> onDataReceived)
    {
        Thread thread = new Thread(() -> loopFunction(onDataReceived));
        thread.start();
    }

    private void recreateSocketListener()
    {
        this.nodeListener.destroy();
        this.nodeListener = null;
        while(this.nodeListener == null)
        {
            try {
                this.nodeListener = new SocketListener(SOCKET_NUMBER);
                slidingWindow.descrease();
            } catch (IOException e1) {
                e1.printStackTrace();
                try {
                    Thread.sleep(slidingWindow.increase());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void loopFunction(Consumer<NodeParseData> onDataReceived)
    {
        DataParser dataParser = new DataParser();
        while(!isEnded) {
            try {
                String message = this.nodeListener.getNextMessage();
                if (message == null)
                    continue;
                NodeParseData data = dataParser.parse(message);
                if (data != null) {
                    onDataReceived.accept(data);
                    slidingWindow.descrease();
                }
            } catch (Exception e) {
                try {
                    Thread.sleep(slidingWindow.increase());
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                this.recreateSocketListener();
                e.printStackTrace();
            }
        }
        this.nodeListener.destroy();
    }

    public void destroy()
    {
        isEnded = true;
        this.nodeListener.destroy();
    }
}
