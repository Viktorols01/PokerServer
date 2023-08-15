package client;

import comms.Connection;
import comms.Protocol;
import poker.PokerModel;

public abstract class PokerClient {
    private Connection connection;
    private Thread thread;

    private PokerModel model;

    private Printer printer;

    public PokerClient(boolean verbose) {
        this.printer = new Printer(verbose);
        this.setup();
    }

    public void connect(String ip, int port) {
        this.connection = new Connection(ip, port);
    }

    protected abstract void setup();

    protected abstract String[] getName();

    protected abstract String[] getType();

    protected abstract String[] getMove(PokerModel model);

    protected abstract void display(PokerModel model);

    protected abstract void parseMessage(String message);

    public final void start() {
        this.thread = new Thread(() -> {
            while (true) {
                Protocol.Command command = Protocol.readCommand(connection);
                printer.print("Received: " + command);
                switch (command) {
                    case REQUEST_NAME: {
                        String[] arguments = getName();
                        send(Protocol.Command.SEND_NAME, arguments);
                        break;
                    }
                    case REQUEST_TYPE: {
                        String[] arguments = getType();
                        send(Protocol.Command.SEND_TYPE, arguments);
                        break;
                    }
                    case REQUEST_MOVE: {
                        String[] arguments = getMove(model);
                        send(Protocol.Command.SEND_MOVE, arguments);
                        break;
                    }
                    case ACCEPTED:
                        break;
                    case DENIED: {
                        break;
                    }
                    case SEND_POKERSTATE: {
                        String[] arguments = Protocol.readArguments(command, connection);
                        this.model = new PokerModel(arguments);
                        display(model);
                        break;
                    }
                    case SEND_MESSAGE: {
                        String[] arguments = Protocol.readArguments(command, connection);
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

    private void send(Protocol.Command command, String[] arguments) {
        printer.print("Sent: " + command);
        Protocol.sendPackage(command, arguments, connection);
    }

    public PokerModel getModel() {
        return this.model;
    }

    private class Printer {
        private boolean print;

        public Printer(boolean print) {
            this.print = print;
        }

        public void print(String message) {
            if (this.print) {
                System.out.println(message);
            }
        }
    }
}
