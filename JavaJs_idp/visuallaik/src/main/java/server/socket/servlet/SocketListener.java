package server.socket.servlet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketListener {

    private ServerSocket ssocket;
    private Socket socket;
    private boolean isEnded;

    public SocketListener(int socketNumber) throws IOException {
        isEnded = false;
        socket = null;
        ssocket = new ServerSocket(socketNumber);
        ssocket.setSoTimeout(10000);
        ssocket.setReuseAddress(true);
    }

    private Socket tryAcceptConnection() throws IOException {
        if (socket != null)
            if (socket.isConnected() && !socket.isClosed())
                return socket;
        socket = ssocket.accept();
        socket.setKeepAlive(true);
        socket.setSoTimeout(10000);
        socket.setReuseAddress(true);
        return socket;
    }

    private static final int BUFFER_SIZE = 2048;
    private String readStreamMessage(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        int byteValue;
        ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        while ((byteValue = inputStream.read()) != -1 && !isEnded) {
            if (byteValue == '\0') {
                // skip series of trailing symbols
                if (byteBuffer.position() == 0)
                    continue;
                String message = new String(byteBuffer.array(), 0, byteBuffer.position(), StandardCharsets.UTF_8);
                byteBuffer.clear();
                return message;
            } else
                byteBuffer.put((byte)byteValue);
        }
        inputStream.close();
        socket.close();
        if (byteBuffer.position() > 0) {
            return new String(byteBuffer.array(), 0, byteBuffer.position(), StandardCharsets.UTF_8);
        }
        return null;
    }

    public String getNextMessage() throws IOException {
        String message = null;
        while(message == null) {
            Socket socket = tryAcceptConnection();
            if (socket == null) {
                message = null;
                continue;
            }

            if(socket.isConnected() && !socket.isClosed() && !isEnded) {
                message = readStreamMessage(socket);
            }
        }
        return message;
    }

    public void destroy()
    {
        isEnded = true;
        if (socket != null)
            if (socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        if (ssocket != null)
            if (ssocket != null)
                if (!ssocket.isClosed()) {
                    try {
                        ssocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

    }
}
