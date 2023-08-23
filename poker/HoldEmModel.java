package poker;

import java.util.ArrayList;

public class HoldEmModel {
    private ArrayList<PlayerData> players;
    private PlayerData toPlay;
    private CardCollection communityCards;

    private int pot;
    private int smallBlind;

    private PlayerData you;
    private int remainingBet;

    private int minBet;

    public HoldEmModel(String[] arguments) {
        this.players = new ArrayList<PlayerData>();
        int j = 0;

        int playercount = Integer.valueOf(arguments[j++]);
        for (int i = 0; i < playercount; i++) {
            String name = arguments[j++];
            int markers = Integer.valueOf(arguments[j++]);
            int bettedmarkers = Integer.valueOf(arguments[j++]);
            String blind = arguments[j++];
            boolean folded = Boolean.valueOf(arguments[j++]);
            boolean yourturn = Boolean.valueOf(arguments[j++]);
            boolean you = Boolean.valueOf(arguments[j++]);
            String card1 = arguments[j++];
            String card2 = arguments[j++];

            CardCollection hand = new CardCollection();
            if (!card1.equals("none") && !card2.equals("none")) {
                hand.add(new Card(card1));
                hand.add(new Card(card2));
            } else {
                hand.add(null);
                hand.add(null);
            }
            PlayerData player = new PlayerData(name, hand, markers, bettedmarkers, folded, blind);
            this.players.add(player);
            if (yourturn) {
                this.toPlay = player;
            }
            if (you) {
                this.you = player;
            }
        }

        this.communityCards = new CardCollection();
        int cardcount = Integer.valueOf(arguments[j++]);
        for (int i = 0; i < cardcount; i++) {
            this.communityCards.add(new Card(arguments[j++]));
        }

        this.smallBlind = Integer.valueOf(arguments[j++]);
        this.minBet = Integer.valueOf(arguments[j++]);

        calculate();
    }

    private void calculate() {
        this.pot = 0;
        for (PlayerData player : players) {
            this.pot += player.getBettedMarkers();
        }

        if (this.you != null) {
            this.remainingBet = this.minBet - this.you.getBettedMarkers();
        }
    }

    public ArrayList<PlayerData> getPlayers() {
        return this.players;
    }

    public PlayerData getToPlay() {
        return this.toPlay;
    }

    public PlayerData getYou() {
        return this.you;
    }

    public CardCollection getCommunityCards() {
        return this.communityCards;
    }

    public int getPot() {
        return this.pot;
    }

    public int getRemainingBet() {
        return this.remainingBet;
    }

    public int getSmallBlind() {
        return this.smallBlind;
    }

}
