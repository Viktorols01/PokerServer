package client;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.Scanner;

import comms.Connection;
import comms.Protocol;
import poker.PokerModel;

public class Client {
    private Connection connection;
    private Thread thread;

    private PokerBot bot;
    private PokerModel model;
    private boolean auto;

    private Printer printer;

    public Client() {
        this.auto = false;
        this.printer = new Printer(true);
    }

    public Client(boolean auto, boolean print) {
        if (auto) {
            this.bot = new PokerBot() {
                @Override
                public String[] getMove(PokerModel model) {
                    return check();
                }
            };
        }
        this.auto = true;
        this.printer = new Printer(print);
    }

    public Client(PokerBot bot, boolean print) {
        this.bot = bot;
        this.auto = true;
        this.printer = new Printer(print);
    }

    public void connect(String ip, int port) {
        InetSocketAddress address = new InetSocketAddress("localhost", port);
        this.connection = new Connection(address);
    }

    public void start() {
        this.thread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            loop: while (true) {
                Protocol.Command command = Protocol.readCommand(connection);
                switch (command) {
                    case REQUEST_NAME:
                        printer.print("Name requested.");
                        printer.print("Asking for name...");
                        String name;
                        if (auto) {
                            int n = new Random().nextInt(99);
                            name = ("Bot " + n);
                            Protocol.sendPackage(Protocol.Command.SEND_NAME, new String[] { name }, connection);
                        } else {
                            System.out.println("Enter a name:");
                            name = scanner.nextLine();
                            Protocol.sendPackage(Protocol.Command.SEND_NAME, new String[] { name }, connection);
                        }
                        printer.print("Asking for name (" + name + ")...");
                        break;
                    case REQUEST_TYPE:
                        printer.print("Type requested.");
                        printer.print("Asking for type (player)...");
                        Protocol.sendPackage(Protocol.Command.SEND_TYPE, new String[] { "player" }, connection);
                        break;
                    case REQUEST_MOVE:
                        printer.print("Move requested.");
                        if (auto) {
                            String[] arguments = bot.getMove(model);
                            Protocol.sendPackage(Protocol.Command.SEND_MOVE, arguments, connection);
                        } else {
                            System.out.println("Make a move:");
                            String input = scanner.nextLine();
                            String[] split = input.split(" ");
                            String move = split[0].toLowerCase();
                            switch (move) {
                                case "check":
                                    printer.print("Asking for move (check)...");
                                    Protocol.sendPackage(Protocol.Command.SEND_MOVE, new String[] { "check", "0" },
                                            connection);
                                    break;
                                case "raise":
                                    int n = Integer.valueOf(split[1]);
                                    printer.print("Asking for move (raise " + n + ")...");
                                    Protocol.sendPackage(Protocol.Command.SEND_MOVE,
                                            new String[] { "raise", String.valueOf(n) },
                                            connection);
                                    break;
                                default:
                                    printer.print("Asking for move (fold)...");
                                    Protocol.sendPackage(Protocol.Command.SEND_MOVE, new String[] { "fold", "0" },
                                            connection);
                                    break;
                            }
                        }
                        break;
                    case ACCEPTED:
                        printer.print("Accepted!");
                        break;
                    case DENIED: {
                        String[] arguments = Protocol.readArguments(command, connection);
                        printer.print("Denied! (" + arguments[0] + ")");
                        break;
                    }
                    case SEND_POKERSTATE: {
                        String[] arguments = Protocol.readArguments(command, connection);
                        this.model = new PokerModel(arguments);
                        printer.print(this.model.toString());
                        break;
                    }
                    case SEND_MESSAGE: {
                        String[] arguments = Protocol.readArguments(command, connection);
                        String message = arguments[0];
                        printer.print(message);
                        break;
                    }
                    default:
                        String[] arguments = Protocol.readArguments(command, connection);
                        printer.print(command.toString());
                        for (String str : arguments) {
                            printer.print(str);
                        }
                        break loop;
                }
            }
            scanner.close();
        });
        this.thread.start();
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
