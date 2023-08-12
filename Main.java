import comms.PokerServer;

class Main {
    public static void main(String[] args) {
        PokerServer server = new PokerServer();
        server.getConnections();
        System.out.println(server.getLocalPort());
    }
}