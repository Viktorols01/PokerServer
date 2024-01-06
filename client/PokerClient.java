package client;

import javax.swing.JOptionPane;

import comms.Connection;
import poker.HoldEmModel;
import protocol.ProtocolCommand;
import protocol.ProtocolHandler;

public abstract class PokerClient {
    private Connection connection;
    private Thread readThread;
    private ProtocolHandler protocolHandler;

    private HoldEmModel model;

    public PokerClient(boolean verbose) {
        this.protocolHandler = new ProtocolHandler("Client", verbose);
        this.setup();
    }

    public void connect(String ip, int port) {
        this.connection = new Connection(ip, port);
        this.connection.setName("Server");
        this.start();
    }

    protected abstract void setup();

    protected abstract String[] getName();

    protected abstract String[] getType();

    protected abstract String[] getMove(HoldEmModel model);

    protected abstract String[] getContinue();

    protected abstract void display(HoldEmModel model);

    protected abstract void parseMessage(String message);

    private final void start() {
        this.readThread = new Thread(() -> {
            while (true) {
                ProtocolCommand command = protocolHandler.readCommand(connection);
                switch (command) {
                    case REQUEST_NAME: {
                        String[] arguments = getName();
                        protocolHandler.sendPackage(ProtocolCommand.SEND_NAME, arguments, connection);
                        break;
                    }
                    case ACCEPTED_JOIN: {
                        break;
                    }
                    case DENIED_JOIN: {
                        String[] arguments = protocolHandler.readArguments(ProtocolCommand.DENIED_JOIN, connection);
                        JOptionPane.showMessageDialog(null, "Your client has been denied! Reason:" + arguments[0]);
                        break;
                    }
                    case REQUEST_TYPE: {
                        String[] arguments = getType();
                        protocolHandler.sendPackage(ProtocolCommand.SEND_TYPE, arguments, connection);
                        break;
                    }
                    case REQUEST_MOVE: {
                        String[] arguments = getMove(model);
                        protocolHandler.sendPackage(ProtocolCommand.SEND_MOVE, arguments, connection);
                        break;
                    }
                    case DENIED_MOVE: {
                        String[] arguments = protocolHandler.readArguments(ProtocolCommand.DENIED_MOVE, connection);
                        JOptionPane.showMessageDialog(null,
                                "Your clients move has been denied! Reason: " + arguments[0]);
                        break;
                    }
                    case REQUEST_CONTINUE: {
                        String[] arguments = getContinue();
                        protocolHandler.sendPackage(command, arguments, connection);
                        break;
                    }
                    case SEND_POKERSTATE: {
                        String[] arguments = protocolHandler.readArguments(command, connection);
                        this.model = new HoldEmModel(arguments);
                        display(model);
                        break;
                    }
                    case SEND_MESSAGE: {
                        String[] arguments = protocolHandler.readArguments(command, connection);
                        String message = arguments[0];
                        parseMessage(message);
                        break;
                    }
                    default:
                        break;
                }
            }
        });
        this.readThread.start();
    }

    // private void sendCommand(ProtocolCommand command, String[] arguments) {
    // protocolReader.sendPackage(command, arguments, connection);
    // }

    // private ProtocolCommand readCommand() {
    // ProtocolCommand command = protocolReader.readCommand(connection);
    // return command;
    // }

    // private String[] readArguments(ProtocolCommand command) {
    // String[] arguments = protocolReader.readArguments(command, connection);
    // return arguments;
    // }

    public HoldEmModel getModel() {
        return this.model;
    }
}
