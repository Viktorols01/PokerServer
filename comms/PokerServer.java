package comms;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class PokerServer {
    ServerSocket serversocket;
    ArrayList<Socket> sockets;

    Thread connectionListener;

    public PokerServer() {
        try {
            this.serversocket = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getConnections() {
        this.sockets = new ArrayList<Socket>();
        System.out.println("Listening for connections...");
        this.connectionListener = new Thread(() -> {
            while (true) {
                Socket socket;
                try {
                    socket = serversocket.accept();
                    sockets.add(socket);
                    System.out.println(socket.getInetAddress().getHostAddress() + " connected.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "connectionListener");
        this.connectionListener.start();
    }

    public int getLocalPort() {
        return serversocket.getLocalPort();
    }

    public InetAddress getInetAddress() {
        return serversocket.getInetAddress();
    }
}
