package comms;

import poker.CardCollection;

public class PokerPlayer {
    private Connection connection;
    private String name;

    private CardCollection hand;
    private int markers;

    public PokerPlayer(Connection connection, String name) {
        this.connection = connection;
        this.name = name;
        this.hand = new CardCollection();
        this.markers = 1000;
    }

    public String getName() {
        return this.name;
    }

    public CardCollection getHand() {
        return this.hand;
    }

    public int getMarkers() {
        return this.markers;
    }

    public int takeMarkers(int n) {
        if (n > this.markers) {
            n = this.markers;
            this.markers = 0;
        } else if (n < 0) {
            n = 0;
        } else {
            this.markers -= n;
        }
        return n;
    }

    public void giveMarkers(int n) {
        this.markers += n;
    }

    public String getIP() {
        return this.connection.getSocket().getInetAddress().getHostAddress();
    }
}
