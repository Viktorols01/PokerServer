package client;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.Scanner;

import comms.Connection;
import comms.Protocol;
import poker.PokerModel;

public class Client {
    private Connection connection;

    private PokerBot bot;
    private PokerModel model;

    private boolean auto;

    public Client() {
        this.bot = new PokerBot() {
            @Override
            public String[] getMove(PokerModel model) {
                return check();
            }
        };
        this.auto = false;
    }

    public Client(boolean auto) {
        this.bot = new PokerBot() {
            @Override
            public String[] getMove(PokerModel model) {
                return check();
            }
        };
        this.auto = auto;
    }

    public void connect(String ip, int port) {
        InetSocketAddress address = new InetSocketAddress("localhost", port);
        this.connection = new Connection(address);
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        loop: while (true) {
            Protocol.Command command = Protocol.readCommand(connection);
            switch (command) {
                case REQUEST_NAME:
                    System.out.println("Name requested.");
                    System.out.println("Asking for name...");
                    String name;
                    if (auto) {
                        int n = new Random().nextInt(99);
                        name = ("Client " + n);
                        Protocol.sendPackage(Protocol.Command.SEND_NAME, new String[] { name }, connection);
                    } else {
                        System.out.println("Enter a name:");
                        name = scanner.nextLine();
                        Protocol.sendPackage(Protocol.Command.SEND_NAME, new String[] { name }, connection);
                    }
                    System.out.println("Asking for name (" + name + ")...");
                    break;
                case REQUEST_TYPE:
                    System.out.println("Type requested.");
                    System.out.println("Asking for type (player)...");
                    Protocol.sendPackage(Protocol.Command.SEND_TYPE, new String[] { "player" }, connection);
                    break;
                case REQUEST_MOVE:
                    System.out.println("Move requested.");
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
                                System.out.println("Asking for move (check)...");
                                Protocol.sendPackage(Protocol.Command.SEND_MOVE, new String[] { "check", "0" },
                                        connection);
                                break;
                            case "raise":
                                int n = Integer.valueOf(split[1]);
                                System.out.println("Asking for move (raise " + n + ")...");
                                Protocol.sendPackage(Protocol.Command.SEND_MOVE,
                                        new String[] { "raise", String.valueOf(n) },
                                        connection);
                                break;
                            default:
                                System.out.println("Asking for move (fold)...");
                                Protocol.sendPackage(Protocol.Command.SEND_MOVE, new String[] { "fold", "0" },
                                        connection);
                                break;
                        }
                    }
                    break;
                case ACCEPTED:
                    System.out.println("Accepted!");
                    break;
                case DENIED: {
                    String[] arguments = Protocol.readArguments(command, connection);
                    System.out.println("Denied! (" + arguments[0] + ")");
                    break;
                }
                case SEND_POKERSTATE: {
                    String[] arguments = Protocol.readArguments(command, connection);
                    this.model = new PokerModel(arguments);
                    System.out.println(this.model);
                    break;
                }
                default:
                    String[] arguments = Protocol.readArguments(command, connection);
                    System.out.println(command);
                    for (String str : arguments) {
                        System.out.println(str);
                    }
                    break loop;
            }
        }
        scanner.close();
    }
}
