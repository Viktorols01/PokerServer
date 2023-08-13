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
    private ServerSocket serversocket;
    private List<Connection> connections;
    private boolean open;

    private Thread joinListener;
    private List<Thread> connectionListeners;

    public Server() {
        try {
            this.serversocket = new ServerSocket(50160);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.connections = Collections.synchronizedList(new ArrayList<Connection>());

        this.connectionListeners = Collections.synchronizedList(new ArrayList<Thread>());
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

    private void joinListen() {
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

    private void listen(Connection connection) {
        Protocol.Command command = Protocol.readCommand(connection);
        readCommand(connection, command);
    }

    protected abstract void readCommand(Connection connection, Protocol.Command command);

    private void addConnection(Connection connection, String name) {
        connection.setName(name);
        connections.add(connection);
        Thread thread = new Thread(() -> {
            listen(connection);
        });
        thread.start();
        connectionListeners.add(thread);
        System.out
                .println(connection.getSocket().getInetAddress().getHostAddress() + " connected as " + name
                        + ".");
        Protocol.sendPackage(Protocol.Command.ACCEPTED, new String[0], connection);
    }

    private static void rejectConnection(Connection connection, String reason) {
        Protocol.sendPackage(Protocol.Command.DENIED, new String[] { reason }, connection);
        connection.close();
    }

    protected static void setType(Connection connection, Type type) {
        connection.setType(type);
        System.out.println(connection.getName() + " changed to " + type);
        Protocol.sendPackage(Protocol.Command.ACCEPTED, new String[] {}, connection);
    }

    public int getLocalPort() {
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
        str.append("IP: " + getInetAddress() + "\n");
        str.append("Port: " + getLocalPort() + "\n");
        str.append("Connected clients:" + "\n");
        for (Connection connection : connections) {
            str.append(connection.getIP() + ": " + connection.getName() + "\n");
        }
        return str.toString();
    }
}
