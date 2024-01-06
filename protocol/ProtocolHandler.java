package protocol;

import java.util.Stack;

import comms.Connection;

// PACKAGE

// COMMAND (String)
// ARGUMENTS (String[])

public class ProtocolHandler {

    private String name;
    private boolean verbose;

    public ProtocolHandler() {
        this.verbose = false;
    }

    public ProtocolHandler(String name, boolean verbose) {
        this.name = name;
        this.verbose = verbose;
    }

    private ProtocolCommand readCommand(Connection connection) {
        ProtocolCommand command;
        while (connection.hasNextLine()) {
            try {
                command = ProtocolCommand.valueOf(connection.nextLine().toUpperCase());
                return command;
            } catch (IllegalArgumentException e) {
                return ProtocolCommand.UNKNOWN_COMMAND;
            }
        }
        return ProtocolCommand.UNKNOWN_COMMAND;
    }

    private String[] readArguments(ProtocolCommand command, Connection connection) {
        String[] arguments;

        switch (command) {
            case REQUEST_NAME:
                arguments = new String[] {};
                break;
            case SEND_NAME: {
                String name = connection.nextLine();
                arguments = new String[] { name };
                break;
            }
            case ACCEPTED_JOIN:
                arguments = new String[] {};
                break;
            case DENIED_JOIN: {
                String reason = connection.nextLine();
                arguments = new String[] { reason };
                break;
            }
            case REQUEST_TYPE:
                arguments = new String[] {};
                break;
            case SEND_TYPE:
                String type = connection.nextLine();
                arguments = new String[] { type };
                break;
            case ACCEPTED_TYPE: {
                arguments = new String[] {};
                break;
            }
            case DENIED_TYPE: {
                String reason = connection.nextLine();
                arguments = new String[] { reason };
                break;
            }
            case SEND_POKERSTATE:
                Stack<String> arguments_stack = new Stack<String>();

                String playercount = connection.nextLine();
                arguments_stack.push(playercount);
                for (int i = 0; i < Integer.valueOf(playercount); i++) {
                    String name = connection.nextLine();
                    String markers = connection.nextLine();
                    String bettedMarkers = connection.nextLine();
                    String blind = connection.nextLine();
                    String folded = connection.nextLine();
                    String yourturn = connection.nextLine();
                    String you = connection.nextLine();
                    String card1 = connection.nextLine();
                    String card2 = connection.nextLine();
                    arguments_stack.push(name);
                    arguments_stack.push(markers);
                    arguments_stack.push(bettedMarkers);
                    arguments_stack.push(blind);
                    arguments_stack.push(folded);
                    arguments_stack.push(yourturn);
                    arguments_stack.push(you);
                    arguments_stack.push(card1);
                    arguments_stack.push(card2);
                }

                String cardcount = connection.nextLine();
                arguments_stack.push(cardcount);
                for (int i = 0; i < Integer.valueOf(cardcount); i++) {
                    String card = connection.nextLine();
                    arguments_stack.push(card);
                }

                String blind = connection.nextLine();
                String minBet = connection.nextLine();
                arguments_stack.push(blind);
                arguments_stack.push(minBet);

                arguments = arguments_stack.toArray(String[]::new);
                break;
            case REQUEST_CONTINUE:
                arguments = new String[] {};
                break;
            case SEND_CONTINUE:
                arguments = new String[] {};
                break;
            case REQUEST_MOVE:
                arguments = new String[] {};
                break;
            case SEND_MOVE:
                String move = connection.nextLine();
                String amount = connection.nextLine();
                arguments = new String[] { move, amount };
                break;
            case ACCEPTED_MOVE: {
                arguments = new String[] {};
                break;
            }
            case DENIED_MOVE: {
                String reason = connection.nextLine();
                arguments = new String[] { reason };
                break;
            }
            case SEND_MESSAGE:
                String message = connection.nextLine();
                arguments = new String[] { message };
                break;

            case UNKNOWN_COMMAND:
                arguments = new String[] {};
                break;
            default:
                arguments = new String[] {};
                break;
        }
        return arguments;
    }

    public ProtocolPackage readPackage(Connection connection) {
        ProtocolCommand command = readCommand(connection);
        String[] arguments = readArguments(command, connection);
        printReceive(command, arguments, connection);
        return new ProtocolPackage(command, arguments);
    }

    public void sendPackage(ProtocolPackage pkg, Connection connection) {
        StringBuilder str = new StringBuilder(pkg.command.name());
        for (String arg : pkg.arguments) {
            str.append("\n");
            str.append(arg);
        }
        printSend(pkg.command, pkg.arguments, connection);
        connection.write(str.toString());
    }

    public void sendPackage(ProtocolCommand command, String[] arguments, Connection connection) {
        StringBuilder str = new StringBuilder(command.name());
        for (String arg : arguments) {
            str.append("\n");
            str.append(arg);
        }
        printSend(command, arguments, connection);
        connection.write(str.toString());
    }

    private void printReceive(ProtocolCommand command, String[] arguments, Connection connection) {
        if (verbose) {
            System.out.println("\u001b[22m" + name + " read from " + connection.getName() + ":" + "\u001b[0m");
            System.out.println("\t" + command.toString());
            for (String argument : arguments) {
                System.out.println("\t" + argument);
            }
            System.out.println("");
        }
    }

    private void printSend(ProtocolCommand command, String[] arguments, Connection connection) {
        if (verbose) {
            System.out.println("\u001b[22m" + name + " sent to " + connection.getName() + ":" + "\u001b[0m");
            System.out.println("\t" + command.toString());
            for (String argument : arguments) {
                System.out.println("\t" + argument);
            }
            System.out.println("");
        }
    }
}