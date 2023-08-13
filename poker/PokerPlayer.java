package poker;

import comms.Connection;

public class PokerPlayer {
    private Connection connection;

    private CardCollection hand;
    private int markers;
    private int bettedMarkers;
    private boolean folded;

    private String blind;

    public PokerPlayer(Connection connection, int markers) {
        this.connection = connection;
        this.hand = new CardCollection();
        this.markers = markers;
        this.blind = "none";
    }

    public String getName() {
        return this.connection.getName();
    }

    public CardCollection getHand() {
        return this.hand;
    }

    public int getMarkers() {
        return this.markers;
    }

    public int getBettedMarkers() {
        return this.bettedMarkers;
    }

    private int takeMarkers(int n) {
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

    public int emptyBettedMarkers() {
        int n = this.bettedMarkers;
        this.bettedMarkers = 0;
        return n;
    }

    public void giveMarkers(int n) {
        this.markers += n;
    }

    public void bet(int n) {
        n = takeMarkers(n);
        this.bettedMarkers += n;
    }

    public String getBlind() {
        return this.blind;
    }

    public void setBlind(String s) {
        this.blind = s;
    }

    public boolean hasFolded() {
        return folded;
    }

    public void setFolded(boolean folded) {
        this.folded = folded;
    }

    public String getIP() {
        return this.connection.getSocket().getInetAddress().getHostAddress();
    }

    public Connection getConnection() {
        return this.connection;
    }
}
