package poker;

import java.util.List;
import java.util.Stack;

import comms.Connection;
import comms.Protocol;

public class TexasHoldEm {
    private PokerServer server;
    private List<PokerPlayer> players;
    private PokerPlayer next;

    private Deck deck;
    private CardCollection communityCards;
    private CardCollection discardPile;

    private int pot;
    private int smallBlind;

    public TexasHoldEm(PokerServer server) {
        this.server = server;
        this.players = server.getPlayers();
    }

    public void round() {
        start();
        deal();
        sendGameInfo();
    }

    private void start() {
        this.deck = new Deck();
        this.deck.shuffle(0);
        this.communityCards = new CardCollection();

        this.pot = 0;
        this.smallBlind = 50;
    }

    private void deal() {
        for (PokerPlayer player : players) {
            player.getHand().add(deck.draw());
            player.getHand().add(deck.draw());
        }
    }

    private void flop() {
        this.discardPile.add(deck.draw());
        this.communityCards.add(deck.draw());
        this.communityCards.add(deck.draw());
        this.communityCards.add(deck.draw());
    }

    private void turn() {
        this.discardPile.add(deck.draw());
        this.communityCards.add(deck.draw());
    }

    private void river() {
        this.discardPile.add(deck.draw());
        this.communityCards.add(deck.draw());
    }

    private CardCollection getCommunityCards() {
        return communityCards;
    }

    private String[] toPokerState(Connection connection) {
        String playercount = String.valueOf(players.size());
        String cardcount = String.valueOf(communityCards.size());

        Stack<String> arguments = new Stack<String>();

        arguments.push(playercount);
        for (PokerPlayer player : players) {
            String name = player.getName();
            String markers = String.valueOf(player.getMarkers());
            String bettedMarkers = String.valueOf(player.getBettedMarkers());
            String blind = player.getBlind();
            String folded = String.valueOf(player.hasFolded());
            String yourturn = String.valueOf(player == next);
            boolean isYou = player.getConnection() == connection;
            String you = String.valueOf(isYou);
            String card1;
            String card2;
            if (isYou || connection.getType() == Connection.Type.SPECTATOR) {
                card1 = player.getHand().get(0).toCode();
                card2 = player.getHand().get(1).toCode();
            } else {
                card1 = "none";
                card2 = "none";
            }
            arguments.push(name);
            arguments.push(markers);
            arguments.push(bettedMarkers);
            arguments.push(blind);
            arguments.push(folded);
            arguments.push(you);
            arguments.push(yourturn);
            arguments.push(card1);
            arguments.push(card2);
        }

        arguments.push(cardcount);
        for (Card card : communityCards) {
            String cardcode = card.toCode();
            arguments.push(cardcode);
        }

        arguments.push(Integer.toString(pot));
        arguments.push(Integer.toString(smallBlind));

        return arguments.toArray(String[]::new);
    }

    private void sendGameInfo() {
        for (Connection connection : server.getConnections()) {
            String[] arguments = toPokerState(connection);
            Protocol.sendPackage(Protocol.Command.SEND_POKERSTATE, arguments, connection);
        }
    }

}
