package server;

import comms.Connection;
import comms.Connection.Type;
import protocol.ProtocolCommand;
import protocol.ProtocolHandler;
import tools.Broadcaster;
import comms.ConnectionServer;

public class PokerServer extends ConnectionServer {

    private HoldEm game;
    private Thread gameThread;
    private Broadcaster joinedSender;

    private ProtocolHandler protocolHandler;

    public PokerServer(int port) {
        super(port);
        this.game = new HoldEm(this);
        this.joinedSender = new Broadcaster();
        this.protocolHandler = new ProtocolHandler("Server", true);
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
        accept(connection, name);
        protocolHandler.sendPackage(ProtocolCommand.ACCEPTED_JOIN, new String[] {}, connection);
    }

    protected final void rejectConnection(Connection connection, String reason) {
        protocolHandler.sendPackage(ProtocolCommand.DENIED_JOIN, new String[] { reason }, connection);
        connection.close();
    }

    protected final void setType(Connection connection, Type type) {
        connection.setType(type);
        protocolHandler.sendPackage(ProtocolCommand.ACCEPTED_TYPE, new String[] {}, connection);
    }

    @Override
    protected void joinHandle(Connection connection) {
        if (!this.open) {
            rejectConnection(connection, connection.getInetAddress().getHostAddress()
                    + " tried to connect but server is closed.");
            return;
        }

        protocolHandler.sendPackage(ProtocolCommand.REQUEST_NAME, new String[0], connection);
        try {
            ProtocolCommand command = protocolHandler.readCommand(connection);
            if (command == ProtocolCommand.SEND_NAME) {
                String name = protocolHandler.readArguments(command, connection)[0];
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
        protocolHandler.sendPackage(ProtocolCommand.REQUEST_TYPE, new String[0], connection);
        try {
            ProtocolCommand command = protocolHandler.readCommand(connection);
            if (command == ProtocolCommand.SEND_TYPE) {
                if (connection.getType() == null) {
                    String[] arguments = protocolHandler.readArguments(ProtocolCommand.SEND_TYPE, connection);
                    Connection.Type type = Connection.Type.valueOf(arguments[0].toUpperCase());
                    switch (type) {
                        case PLAYER:
                            setType(connection, type);
                            break;
                        case SPECTATOR:
                            setType(connection, type);
                            break;
                        default:
                        protocolHandler.sendPackage(ProtocolCommand.DENIED_TYPE, new String[] { "Invalid type" },
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