package comms;

import java.util.Stack;

// PACKAGE

// COMMAND (String)
// ARGUMENTS (String[])

public class Protocol {
    public enum Command {
        REQUEST_NAME,
        SEND_NAME,

        SEND_TYPE,

        SEND_POKERSTATE,

        ACCEPTED,
        DENIED,

        UNKNOWN_COMMAND;
    }

    public static Command readCommand(Connection connection) {
        Command command = Command.valueOf(connection.receive().toUpperCase());
        return command;
    }

    public static String[] readArguments(Command command, Connection connection) {
        switch (command) {
            case REQUEST_NAME:
                return new String[] {};
            case SEND_NAME:
            {
                String name = connection.receive();
                return new String[] { name };
            }
            case SEND_TYPE:
                String type = connection.receive();
                return new String[] { type };

            case SEND_POKERSTATE: 
                String playercount = connection.receive();
                String cardcount = connection.receive();

                Stack<String> arguments = new Stack<String>();

                arguments.push(playercount);
                for (int i = 0; i < Integer.valueOf(playercount); i++) {
                    String name = connection.receive();
                    String markers = connection.receive();
                    String bettedMarkers = connection.receive();
                    String blind = connection.receive();
                    String folded = connection.receive();
                    String yourturn = connection.receive();
                    String you = connection.receive();
                    String card1 = connection.receive();
                    String card2 = connection.receive();
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

                arguments.push(cardcount);
                for (int i = 0; i < Integer.valueOf(cardcount); i++) {
                    String card = connection.receive();
                    arguments.push(card);
                }

                String pot = connection.receive();
                String blind = connection.receive();
                arguments.push(pot);
                arguments.push(blind);

                return (String[])arguments.toArray();

            case ACCEPTED:
                return new String[] {};
            case DENIED:
                String reason = connection.receive();
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