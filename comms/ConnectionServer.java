package comms;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class ConnectionServer {

    protected ServerSocket serversocket;
    protected int port;
    protected List<Connection> connections;

    protected Thread joinListener;
    protected boolean open;

    public ConnectionServer(int port) {
        try {
            this.serversocket = new ServerSocket(port);
            this.port = port;
            this.connections = new ArrayList<Connection>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openConnections() {
        if (!open) {
            this.open = true;
            this.joinListener = new Thread(() -> {
                while (true) {
                    joinListen();
                }
            }, "joinListener");
            this.joinListener.start();
        }
    }

    public void closeConnections() {
        if (open) {
            this.open = false;
            this.joinListener.interrupt();
        }
    }

    protected void accept(Connection connection, String name) {
        connection.setName(name);
        connections.add(connection);
    }

    protected abstract void joinHandle(Connection connection);

    protected void joinListen() {
        try {
            Socket socket;
            socket = serversocket.accept();
            Connection connection = new Connection(socket);
            joinHandle(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isOpen() {
        return this.open;
    }

    public String getIP() {
        return this.getInetAddress().getHostName();
    }

    public int getPort() {
        return serversocket.getLocalPort();
    }

    public InetAddress getInetAddress() {
        return serversocket.getInetAddress();
    }

    public List<Connection> getConnections() {
        return this.connections;
    }
}