package comms;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PokerServer {
    private ServerSocket serversocket;
    private List<PokerPlayer> players;
    private int playerLimit = 10;

    private Thread connectionListener;

    public PokerServer() {
        try {
            this.serversocket = new ServerSocket(50160);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getConnections() {
        this.players = Collections.synchronizedList(new ArrayList<PokerPlayer>());
        System.out.println("Listening for connections...");
        this.connectionListener = new Thread(() -> {
            while (true) {
                try {
                    Socket socket;
                    socket = serversocket.accept();
                    Connection connection = new Connection(socket);

                    System.out.println(socket.getInetAddress().getHostAddress() + " is trying to connect...");
                    Protocol.REQUEST_NAME.send(connection);
                    try {
                        String command = Protocol.readCommand(connection);
                        if (command.equals(("SEND_NAME"))) {
                            String name = Protocol.SEND_NAME.readPackage(connection)[0];
                            boolean nameTaken = false;
                            for (PokerPlayer player : players) {
                                if (player.getName().equals(name)) {
                                    nameTaken = true;
                                }
                            }
                            if (nameTaken) {
                                System.out.println(socket.getInetAddress().getHostAddress() + " tried to connect but "
                                        + name + " was taken.");
                                connection.close();
                            } else {
                                players.add(new PokerPlayer(connection, name));
                                System.out
                                        .println(socket.getInetAddress().getHostAddress() + " connected as " + name
                                                + ".");
                                Protocol.NAME_ACCEPTED.send(connection);
                            }
                        } else {
                            System.out.println(socket.getInetAddress().getHostAddress() + " tried to connect but didn't supply a name.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("IP: " + getInetAddress() + "\n");
        str.append("Port: " + getLocalPort() + "\n");
        str.append("Connected players:" + "\n");
        for (PokerPlayer player : players) {
            str.append(player.getIP() + ": " + player.getName() + "(" + player.getMarkers() + ")" + "\n");
        }
        return str.toString();
    }
}
