package comms;

import java.util.Stack;

// PACKAGE

// COMMAND (String)
// ARGUMENTS (String[])

public class Protocol {
    public enum Command {
        REQUEST_NAME,
        SEND_NAME,

        REQUEST_TYPE,
        SEND_TYPE,

        SEND_POKERSTATE,

        REQUEST_MOVE,
        SEND_MOVE,

        SEND_MESSAGE,

        ACCEPTED,
        DENIED,

        UNKNOWN_COMMAND;
    }

    public static Command readCommand(Connection connection) {
        Command command;
        try {
            command = Command.valueOf(connection.nextLine().toUpperCase());
        } catch (IllegalArgumentException e) {
            command = Command.UNKNOWN_COMMAND;
        }
        return command;
    }

    public static String[] readArguments(Command command, Connection connection) {
        switch (command) {
            case REQUEST_NAME:
                return new String[] {};
            case SEND_NAME: {
                String name = connection.nextLine();
                return new String[] { name };
            }
            case SEND_TYPE:
                String type = connection.nextLine();
                return new String[] { type };

            case SEND_POKERSTATE:
                Stack<String> arguments = new Stack<String>();

                String playercount = connection.nextLine();
                arguments.push(playercount);
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
                    arguments.push(name);
                    arguments.push(markers);
                    arguments.push(bettedMarkers);
                    arguments.push(blind);
                    arguments.push(folded);
                    arguments.push(yourturn);
                    arguments.push(you);
                    arguments.push(card1);
                    arguments.push(card2);
                }

                String cardcount = connection.nextLine();
                arguments.push(cardcount);
                for (int i = 0; i < Integer.valueOf(cardcount); i++) {
                    String card = connection.nextLine();
                    arguments.push(card);
                }

                String blind = connection.nextLine();
                String minBet = connection.nextLine();
                arguments.push(blind);
                arguments.push(minBet);

                return arguments.toArray(String[]::new);

            case REQUEST_MOVE:
                return new String[] {};

            case SEND_MOVE:
                String move = connection.nextLine();
                String amount = connection.nextLine();
                return new String[] { move, amount };

            case SEND_MESSAGE:
                String message = connection.nextLine();
                return new String[] { message };

            case ACCEPTED:
                return new String[] {};
            case DENIED:
                String reason = connection.nextLine();
                return new String[] { reason };
            case UNKNOWN_COMMAND:
                return new String[] {};
            default:
                return new String[] {};
        }
    }

    public static void sendPackage(Command command, String[] arguments, Connection connection) {
        StringBuilder str = new StringBuilder(command.name());
        for (String arg : arguments) {
            str.append("\n");
            str.append(arg);
        }
        connection.write(str.toString());
    }
}