package test;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.Scanner;

import comms.Connection;
import comms.Protocol;
import poker.PokerModel;

public class TestClient {
    Connection connection;

    PokerModel model;

    boolean auto = true;

    public TestClient() {
        int port = 50160;
        InetSocketAddress address = new InetSocketAddress("localhost", port);
        this.connection = new Connection(address);
    }

    public void readCommands() {
        while (true) {
            Protocol.Command command = Protocol.readCommand(connection);
            switch (command) {
                case REQUEST_NAME:
                    System.out.println("Name requested.");
                    System.out.println("Asking for name...");
                    if (auto) {
                        int n = new Random().nextInt(99);
                        Protocol.sendPackage(Protocol.Command.SEND_NAME, new String[] { ("TestClient " + n) }, connection);
                    } else {
                        System.out.println("Enter a name:");
                        Scanner scanner = new Scanner(System.in);
                        String name = scanner.nextLine();
                        Protocol.sendPackage(Protocol.Command.SEND_NAME, new String[] { name }, connection);
                    }
                    System.out.println("Asking to be a player...");
                    Protocol.sendPackage(Protocol.Command.SEND_TYPE, new String[] { "player" }, connection);
                    break;
                case REQUEST_MOVE:
                    System.out.println("Move requested.");
                    if (auto) {
                        Protocol.sendPackage(Protocol.Command.SEND_MOVE, new String[] { "check", "0" }, connection);
                    } else {
                        System.out.println("Make a move:");
                        Scanner scanner = new Scanner(System.in);
                        String move = scanner.nextLine();
                        switch (move) {
                            case "check":
                                Protocol.sendPackage(Protocol.Command.SEND_MOVE, new String[] { "check", "0" },
                                        connection);
                                break;
                            case "raise":
                                System.out.println("Raise amount:");
                                String n = scanner.nextLine();
                                Protocol.sendPackage(Protocol.Command.SEND_MOVE, new String[] { "raise", n },
                                        connection);
                                break;
                            default:
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
                    System.out.println("Denied!");
                    String[] arguments = Protocol.readArguments(command, connection);
                    System.out.println("Reason: " + arguments[0]);
                    break;
                }
                case SEND_POKERSTATE: {
                    String[] arguments = Protocol.readArguments(command, connection);
                    /*
                     * System.out.println("got these:");
                     * for (String arg : arguments) {
                     * System.out.println(arg);
                     * }
                     */
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
                    break;
            }
        }
    }

    public static void main(String[] args) {
        TestClient client = new TestClient();
        client.readCommands();
    }
}
