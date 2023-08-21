package client;

import javax.swing.JOptionPane;

import comms.Connection;
import comms.Protocol;
import poker.HoldEmModel;

public abstract class PokerClient {
    private Connection connection;
    private Thread thread;

    private HoldEmModel model;

    private Printer printer;

    public PokerClient(boolean verbose) {
        this.printer = new Printer(verbose);
        this.setup();
    }

    public void connect(String ip, int port) {
        this.connection = new Connection(ip, port);
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
        this.thread = new Thread(() -> {
            while (true) {
                Protocol.Command command = readCommand();
                switch (command) {
                    case REQUEST_NAME: {
                        String[] arguments = getName();
                        sendCommand(Protocol.Command.SEND_NAME, arguments);
                        break;
                    }
                    case ACCEPTED_JOIN: {
                        break;
                    }
                    case DENIED_JOIN: {
                        String[] arguments = Protocol.readArguments(Protocol.Command.DENIED_JOIN, connection);
                        JOptionPane.showMessageDialog(null, "Your client has been denied! Reason:" + arguments[0]);
                        break;
                    }
                    case REQUEST_TYPE: {
                        String[] arguments = getType();
                        sendCommand(Protocol.Command.SEND_TYPE, arguments);
                        break;
                    }
                    case REQUEST_MOVE: {
                        String[] arguments = getMove(model);
                        sendCommand(Protocol.Command.SEND_MOVE, arguments);
                        break;
                    }
                    case DENIED_MOVE: {
                        String[] arguments = Protocol.readArguments(Protocol.Command.DENIED_MOVE, connection);
                        JOptionPane.showMessageDialog(null,
                                "Your clients move has been denied! Reason:" + arguments[0]);
                        break;
                    }
                    case REQUEST_CONTINUE: {
                        String[] arguments = getContinue();
                        sendCommand(Protocol.Command.SEND_CONTINUE, arguments);
                        break;
                    }
                    case SEND_POKERSTATE: {
                        String[] arguments = readArguments(command);
                        this.model = new HoldEmModel(arguments);
                        display(model);
                        break;
                    }
                    case SEND_MESSAGE: {
                        String[] arguments = readArguments(command);
                        String message = arguments[0];
                        parseMessage(message);
                        break;
                    }
                    default:
                        break;
                }
            }
        });
        this.thread.start();
    }

    private void sendCommand(Protocol.Command command, String[] arguments) {
        printer.print("sent command: " + command);
        Protocol.sendPackage(command, arguments, connection);
    }

    private Protocol.Command readCommand() {
        Protocol.Command command = Protocol.readCommand(connection);
        printer.print("received command: " + command);
        return command;
    }

    private String[] readArguments(Protocol.Command command) {
        String[] arguments = Protocol.readArguments(command, connection);
        StringBuilder str = new StringBuilder();
        str.append("received arguments: ");
        for (String argument : arguments) {
            str.append("\n");
            str.append(argument);
        }
        printer.print(str.toString());
        return arguments;
    }

    public HoldEmModel getModel() {
        return this.model;
    }

    private class Printer {
        private boolean print;

        public Printer(boolean print) {
            this.print = print;
        }

        public void print(String message) {
            if (this.print) {
                System.out.println("Client " + message);
            }
        }
    }
}
