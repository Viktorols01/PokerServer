package comms;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comms.Connection.Type;

public abstract class Server {
    protected ServerSocket serversocket;
    protected List<Connection> connections;
    protected boolean open;

    protected Thread joinListener;

    public Server(int port) {
        try {
            this.serversocket = new ServerSocket(port);
            this.connections = Collections.synchronizedList(new ArrayList<Connection>());
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

    protected void joinListen() {
        try {
            Socket socket;
            socket = serversocket.accept();
            Connection connection = new Connection(socket);

            System.out.println(socket.getInetAddress().getHostAddress() + " is trying to connect...");

            if (!this.open) {
                rejectConnection(connection, socket.getInetAddress().getHostAddress()
                        + " tried to connect but server is closed.");
                return;
            }

            Protocol.sendPackage(Protocol.Command.REQUEST_NAME, new String[0], connection);
            try {
                Protocol.Command command = Protocol.readCommand(connection);
                if (command == Protocol.Command.SEND_NAME) {
                    String name = Protocol.readArguments(command, connection)[0];
                    boolean nameTaken = false;
                    for (Connection c : connections) {
                        if (c.getName().equals(name)) {
                            nameTaken = true;
                        }
                    }
                    if (nameTaken) {
                        rejectConnection(connection, socket.getInetAddress().getHostAddress()
                                + " tried to connect but " + name + " was taken.");
                    } else {
                        addConnection(connection, name);
                    }
                } else {
                    rejectConnection(connection, socket.getInetAddress().getHostAddress()
                            + " tried to connect but didn't supply a name.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void addConnection(Connection connection, String name) {
        connection.setName(name);
        connections.add(connection);
        System.out
                .println(connection.getSocket().getInetAddress().getHostAddress() + " connected as " + name
                        + ".");
        Protocol.sendPackage(Protocol.Command.ACCEPTED, new String[0], connection);
    }

    protected static void rejectConnection(Connection connection, String reason) {
        System.out
                .println(connection.getSocket().getInetAddress().getHostAddress() + " was rejected.");
        Protocol.sendPackage(Protocol.Command.DENIED, new String[] { reason }, connection);
        connection.close();
    }

    protected static void setType(Connection connection, Type type) {
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

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("IP: " + getIP() + "\n");
        str.append("Port: " + getPort() + "\n");
        str.append("Connected clients:" + "\n");
        for (Connection connection : connections) {
            str.append(connection.getIP() + ": " + connection.getName() + "\n");
        }
        return str.toString();
    }
}
