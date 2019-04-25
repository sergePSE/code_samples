package socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import server.socket.servlet.SafeSocketListener;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SocketSender {

    Socket socket;
    OutputStreamWriter outputStreamWriter;

    public SocketSender(String hostname) throws IOException {
        this.socket = new Socket(hostname, SafeSocketListener.SOCKET_NUMBER);
        outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(),
                StandardCharsets.UTF_8);
    }

    public void sendMessages(Collection<String> messages) throws IOException {
        for (String message: messages) {
            // as the stream might be not closed and more messages are sent, separate messages with null symbol
            // however even if there are no trailing symbols, stream can be read with ending of the stream
            outputStreamWriter.write(message);
            outputStreamWriter.write("\0");
        }
    }

    public void sendJsonObjects(Collection<Object> objects) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> stringList = new ArrayList<>();
        for (Object obj: objects) {
            stringList.add(objectMapper.writeValueAsString(obj));
        }
        sendMessages(stringList);
    }

    public void destroy() throws IOException {
        outputStreamWriter.close();
        this.socket.close();
    }
}
