package server;

import java.io.IOException;
import java.net.Socket;

import comms.Broadcaster;
import comms.Connection;
import comms.Protocol;
import comms.Protocol.Command;
import comms.Server;

public class PokerServer extends Server {

    private HoldEm game;
    private Thread gameThread;
    private Broadcaster joinedSender;

    public PokerServer(int port) {
        super(port);
        this.game = new HoldEm(this);
        this.joinedSender = new Broadcaster();
    }

    public void startGame() {
        this.game.setup();

        if (gameThread != null) {
            this.gameThread.interrupt();
        }
        this.gameThread = new Thread(() -> {
            game.play();
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
                        this.joinedSender.broadcast();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HoldEm getGame() {
        return this.game;
    }

    public Broadcaster getJoinedSender() {
        return this.joinedSender;
    }
}