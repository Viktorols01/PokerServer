package poker;

import java.util.ArrayList;

public class PokerModel {
    ArrayList<PlayerData> players;
    PlayerData toPlay;
    PlayerData you;
    CardCollection communityCards;

    int smallBlind;
    int minBet;

    public PokerModel(String[] arguments) {
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
    }

    public ArrayList<PlayerData> getPlayers() {
        return players;
    }

    public PlayerData getToPlay() {
        return toPlay;
    }

    public PlayerData getYou() {
        return you;
    }

    public CardCollection getCommunityCards() {
        return communityCards;
    }

    public int getPot() {
        int pot = 0;
        for (PlayerData player : players) {
            pot += player.getBettedMarkers();
        }
        return pot;
    }

    public int getSmallBlind() {
        return smallBlind;
    }

    public int getMinBet() {
        return minBet;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("---");
        str.append("\n");
        str.append("PokerState: ");
        str.append("\n");
        str.append("Name: " + you.getName());
        str.append("\n");
        str.append("Players: ");
        for (PlayerData player : players) {
            str.append("\n\t");
            if (player.hasFolded()) {
                str.append("\u001b[2m");
            }
            if (player == toPlay) {
                str.append("\u001b[32m");
            }
            if (player == you) {
                str.append("You" + ": " + player.getMarkers() + ":" + player.getBettedMarkers() + " (" + player.getBlind() + " blind)");
            } else {
                str.append(player.getName() + ": " + player.getMarkers() + ":" + player.getBettedMarkers() + " (" + player.getBlind() + " blind)");
            }
            str.append("\u001b[0m");
        }
        str.append("\n");
        str.append("Pot: " + this.getPot());
        str.append("\n");
        str.append("Community cards: ");
        for (Card card : communityCards) {
            str.append("\n\t");
            str.append(card);
        }
        str.append("\n");
        str.append("Your hand: " + HandRank.rank(CardCollection.join(you.getHand(), communityCards)));
        for (Card card : you.getHand()) {
            str.append("\n\t");
            str.append(card);
        }
        str.append("\n");
        str.append("Need to bet: " + (this.minBet - you.getBettedMarkers()));
        str.append("\n");
        str.append("Your markers: " + you.getMarkers() + " (" + you.getBettedMarkers() + ")");
        str.append("\n");
        str.append("---");
        return str.toString();
    }
}
