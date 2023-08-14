package game;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comms.Connection;
import comms.Protocol;
import comms.Protocol.Command;
import comms.Server;

public class PokerServer extends Server {
    private List<PokerPlayer> players;
    private TexasHoldEm game;
    private Thread gameThread;

    public PokerServer(int port) {
        super(port);
        this.players = Collections.synchronizedList(new ArrayList<PokerPlayer>());
    }

    public void start() {
        this.game = new TexasHoldEm(this);
        this.gameThread = new Thread(() -> {
            while (true) {
                game.round();
                if (players.size() == 1) {
                    break;
                }
            }
        }, "gameThread");
        this.gameThread.start();
    }

    @Override
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
            Protocol.sendPackage(Protocol.Command.REQUEST_TYPE, new String[0], connection);
            try {
                Protocol.Command command = Protocol.readCommand(connection);
                if (command == Protocol.Command.SEND_TYPE) {
                    if (connection.getType() == null) {
                        String[] arguments = Protocol.readArguments(Command.SEND_TYPE, connection);
                        Connection.Type type = Connection.Type.valueOf(arguments[0].toUpperCase());
                        switch (type) {
                            case PLAYER:
                                PokerPlayer player = new PokerPlayer(connection, 1000);
                                players.add(player);
                                setType(connection, type);
                                break;
                            case SPECTATOR:
                                setType(connection, type);
                                break;
                            default:
                                Protocol.sendPackage(Protocol.Command.DENIED, new String[] { "Invalid type" }, connection);
                                break;
                        }
                    }
                } 
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<PokerPlayer> getPlayers() {
        return this.players;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(super.toString());
        str.append("Players:" + "\n");
        for (PokerPlayer player : players) {
            str.append(player.getName() + ": " + player.getPlayerData().getMarkers() + " markers \n");
        }
        return str.toString();
    }
}