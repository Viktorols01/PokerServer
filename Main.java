import comms.PokerServer;
import poker.TexasHoldEm;

class Main {
    public static void main(String[] args) {
        PokerServer server = new PokerServer();
        server.getConnections();
        System.out.println(server);
    }

    private static void gameLoop() {
        TexasHoldEm game = new TexasHoldEm();
        while (true) {

        }
    }
}