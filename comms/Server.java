package comms;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import comms.Connection.Type;

public abstract class Server {

    protected ServerSocket serversocket;
    protected List<Connection> connections;

    protected Thread joinListener;
    protected boolean open;

    public Server(int port) {
        try {
            this.serversocket = new ServerSocket(port);
            this.connections = new ArrayList<Connection>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openConnections() {
        this.open = true;
        this.joinListener = new Thread(() -> {
            while (true) {
                joinListen();
            }
        }, "joinListener");
        this.joinListener.start();
        System.out.println("Listening for connections...");
    }

    public void closeConnections() {
        this.open = false;
        this.joinListener.interrupt();
        System.out.println("Listening for connections closed.");

    }

    protected abstract void joinListen();

    protected final void addConnection(Connection connection, String name) {
        connection.setName(name);
        connections.add(connection);
        System.out
                .println(connection.getSocket().getInetAddress().getHostAddress() + " connected as " + name
                        + ".");
        Protocol.sendPackage(Protocol.Command.ACCEPTED, new String[0], connection);
    }

    protected final static void rejectConnection(Connection connection, String reason) {
        System.out
                .println(connection.getSocket().getInetAddress().getHostAddress() + " was rejected.");
        Protocol.sendPackage(Protocol.Command.DENIED, new String[] { reason }, connection);
        connection.close();
    }

    protected final static void setType(Connection connection, Type type) {
        connection.setType(type);
        System.out.println(connection.getName() + " changed to " + type);
        Protocol.sendPackage(Protocol.Command.ACCEPTED, new String[] {}, connection);
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