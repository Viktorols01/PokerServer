package comms;

// COMMAND (String)
// PACKAGE (String[])

public class Protocol {
    public static String readCommand(Connection connection) {
        String command = connection.receive();
        return command;
    }

    public static class REQUEST_NAME {
        public static String[] readPackage(Connection connection) {
            return new String[0];
        }

        public static void send(Connection connection) {
            StringBuilder str = new StringBuilder(command());
            connection.write(str.toString());
        }
        
        public static String command() {
            return "REQUEST_NAME";
        }
    }

    public static class SEND_NAME {
        public static String[] readPackage(Connection connection) {
            String name = connection.receive();
            return new String[]{name};
        }

        public static void send(Connection connection, String name) {
            StringBuilder str = new StringBuilder(command());
            str.append("\n");
            str.append(name);
            connection.write(str.toString());
        }

        public static String command() {
            return "SEND_NAME";
        }
    }

    public static class NAME_ACCEPTED {
        public static String[] readPackage(Connection connection) {
            return new String[0];
        }

        public static void send(Connection connection) {
            StringBuilder str = new StringBuilder(command());
            connection.write(str.toString());
        }
        
        public static String command() {
            return "NAME_ACCEPTED";
        }
    }
}
