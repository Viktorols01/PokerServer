package server;

import java.io.IOException;
import java.net.Socket;

import comms.Broadcaster;
import comms.Connection;
import comms.Connection.Type;
import comms.Protocol;
import comms.Protocol.Command;
import comms.ConnectionServer;

public class PokerServer extends ConnectionServer {

    private HoldEm game;
    private Thread gameThread;
    private Broadcaster joinedSender;

    public PokerServer(int port) {
        super(port);
        this.game = new HoldEm(this);
        this.joinedSender = new Broadcaster();
    }

    public void startGame() {
        this.game.addPlayers();

        if (gameThread != null) {
            this.gameThread.interrupt();
        }
        this.gameThread = new Thread(() -> {
            game.play();
        }, "gameThread");
        this.gameThread.start();
    }

    public void restartGame() {
        if (!this.game.isFinished()) {
            return;
        }
        this.game.resetPlayers();

        if (gameThread != null) {
            this.gameThread.interrupt();
        }
        this.gameThread = new Thread(() -> {
            game.play();
        }, "gameThread");
        this.gameThread.start();
    }

    public HoldEm getGame() {
        return this.game;
    }

    public Broadcaster getJoinedSender() {
        return this.joinedSender;
    }

    protected final void addConnection(Connection connection, String name) {
        connection.setName(name);
        connections.add(connection);
        System.out
                .println(connection.getSocket().getInetAddress().getHostAddress() + " connected as " + name
                        + ".");
        String[] arguments = new String[] {};
        Protocol.sendPackage(Protocol.Command.ACCEPTED_JOIN, arguments, connection);
    }

    protected final static void rejectConnection(Connection connection, String reason) {
        System.out
                .println(connection.getSocket().getInetAddress().getHostAddress() + " was rejected.");
        Protocol.sendPackage(Protocol.Command.DENIED_JOIN, new String[] { reason }, connection);
        connection.close();
    }

    protected final static void setType(Connection connection, Type type) {
        connection.setType(type);
        System.out.println(connection.getName() + " changed to " + type);
        Protocol.sendPackage(Protocol.Command.ACCEPTED_TYPE, new String[] {}, connection);
    }

    @Override
    protected void joinHandle(Connection connection) {
        System.out.println(connection.getInetAddress().getHostAddress() + " is trying to connect...");

        if (!this.open) {
            rejectConnection(connection, connection.getInetAddress().getHostAddress()
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
                    rejectConnection(connection, connection.getInetAddress().getHostAddress()
                            + " tried to connect but " + name + " was taken.");
                } else {
                    addConnection(connection, name);
                    this.joinedSender.broadcast();
                }
            } else {
                rejectConnection(connection, connection.getInetAddress().getHostAddress()
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
                            setType(connection, type);
                            break;
                        case SPECTATOR:
                            setType(connection, type);
                            break;
                        default:
                            Protocol.sendPackage(Protocol.Command.DENIED_TYPE, new String[] { "Invalid type" },
                                    connection);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}