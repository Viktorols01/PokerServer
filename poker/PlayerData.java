package poker;

public class PlayerData {
    private String name;
    private CardCollection hand;
    private int markers;
    private int bettedMarkers;
    private boolean folded;
    private String blind;

    public PlayerData(String name, int markers) {
        this.hand = new CardCollection();
        this.markers = markers;
        this.blind = "none";
    }

    public PlayerData(String name, CardCollection hand, int markers, int bettedMarkers, boolean folded, String blind) {
        this.name = name;
        this.hand = hand;
        this.markers = markers;
        this.bettedMarkers = bettedMarkers;
        this.folded = folded;
        this.blind = blind;
    }

    public void reset(int markers) {
        this.hand = new CardCollection();
        this.markers = markers;
        this.markers = markers;
        this.bettedMarkers = 0;
        this.folded = false;
        this.blind = "none";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int takeBettedMarkers(int n) {
        if (n > this.bettedMarkers) {
            n = this.bettedMarkers;
            this.bettedMarkers = 0;
        } else if (n < 0) {
            n = 0;
        } else {
            this.bettedMarkers -= n;
        }
        return n;
    }

    public void giveMarkers(int n) {
        this.markers += n;
    }

    public int bet(int n) {
        n = takeMarkers(n);
        this.bettedMarkers += n;
        return n;
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

    @Override
    public boolean equals(Object other) {
        if ((other == null) || (getClass() != other.getClass())) {
            return false;
        } else {
            if (this.getName().equals(((PlayerData) other).getName())) {
                return true;
            }
        }
        return false;
    }
}
