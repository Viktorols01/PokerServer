package game;

import java.util.List;
import java.util.Stack;

import comms.Connection;
import comms.Protocol;
import poker.Card;
import poker.CardCollection;
import poker.Deck;
import poker.HandRank;

public class TexasHoldEm {
    private PokerServer server;
    private List<PokerPlayer> players;
    private PokerPlayer next;

    private Deck deck;
    private CardCollection communityCards;
    private CardCollection discardPile;

    private int smallBlind;
    private int minBet;

    public TexasHoldEm(PokerServer server) {
        this.server = server;
        this.players = server.getPlayers();

        this.setupBlinds();
    }

    public void round() {
        start();
        deal();
        betBlinds();
        getBets();
        flop();
        getBets();
        turn();
        getBets();
        river();
        determineWinner();
    }

    private void start() {
        this.deck = new Deck();
        this.deck.shuffle();
        this.communityCards = new CardCollection();
        this.discardPile = new CardCollection();
        resetPlayers();

        this.smallBlind = 50;
        this.minBet = smallBlind * 2;
    }

    private void deal() {
        for (PokerPlayer player : players) {
            player.getPlayerData().getHand().add(deck.draw());
            player.getPlayerData().getHand().add(deck.draw());
        }
    }

    private void setupBlinds() {
        for (int i = 0; i < players.size(); i++) {
            PokerPlayer player = players.get(i);
            if (i == 0) {
                player.getPlayerData().setBlind("big");
            } else if (i == 1) {
                player.getPlayerData().setBlind("small");
            } else {
                player.getPlayerData().setBlind("none");
            }
        }
    }

    private void resetPlayers() {
        for (PokerPlayer player : players) {
            player.getPlayerData().getHand().empty();
            player.getPlayerData().setFolded(false);
        }
    }

    private void betBlinds() {
        for (PokerPlayer player : players) {
            switch (player.getPlayerData().getBlind()) {
                case "small":
                    player.getPlayerData().bet(smallBlind);
                    break;
                case "big":
                    player.getPlayerData().bet(smallBlind * 2);
                    break;
                default:
                    break;
            }
        }
    }

    private void getBets() {
        int betsRemaining = players.size();
        int playerCount = players.size();
        int i = 0;
        while (betsRemaining > 0) {
            PokerPlayer player = players.get(i%playerCount);
            this.next = player;
            sendGameInfo();
            if (player.getPlayerData().hasFolded()) {
                betsRemaining--;
                continue;
            }
            System.out.println("Requesting move from " + player.getConnection().getName());
            Protocol.sendPackage(Protocol.Command.REQUEST_MOVE, new String[] {}, player.getConnection());
            Protocol.Command command = Protocol.readCommand(player.getConnection());
            System.out.println("Got: " + command + " from " + player.getConnection().getName());
            
            if (command == Protocol.Command.SEND_MOVE) {
                String[] arguments = Protocol.readArguments(command, player.getConnection());
                String move = arguments[0];
                int value = Integer.parseInt(arguments[1]);
                value = Math.max(0, value);

                switch (move) {
                    case "check": {
                        int remaining = this.minBet - player.getPlayerData().getBettedMarkers();
                        player.getPlayerData().bet(remaining);
                        Protocol.sendPackage(Protocol.Command.ACCEPTED, new String[] {}, player.getConnection());
                        break;
                    }
                    case "raise": {
                        int remaining = this.minBet - player.getPlayerData().getBettedMarkers();
                        int n = player.getPlayerData().bet(value + remaining);
                        if (n > remaining) {
                            this.minBet += n - remaining;
                            betsRemaining += (playerCount - 1);
                        }
                        Protocol.sendPackage(Protocol.Command.ACCEPTED, new String[] {}, player.getConnection());
                        break;
                    }
                    case "fold": {
                        player.getPlayerData().setFolded(true);
                        Protocol.sendPackage(Protocol.Command.ACCEPTED, new String[] {}, player.getConnection());
                    }
                    default:
                        player.getPlayerData().setFolded(true);
                        Protocol.sendPackage(Protocol.Command.DENIED, new String[] { "Unknown move" },
                                player.getConnection());
                        break;
                }
            } else {
                player.getPlayerData().setFolded(true);
                Protocol.sendPackage(Protocol.Command.DENIED, new String[] { "Wrong command" }, player.getConnection());
            }
            betsRemaining--;
            i++;
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

    private void determineWinner() {
        PokerPlayer winner = null;
        HandRank maxRank = new HandRank();
        for (PokerPlayer player : players) {
            if (player.getPlayerData().hasFolded() || player.getPlayerData().getBettedMarkers() == 0) {
                continue;
            }
            CardCollection hand = CardCollection.join(communityCards, player.getPlayerData().getHand());
            HandRank rank = HandRank.rank(hand);
            if (rank.greaterThan(maxRank)) {
                winner = player;
                maxRank = rank;
            }
        }
        int win = winner.getPlayerData().getBettedMarkers();
        for (PokerPlayer player : players) {
            winner.getPlayerData().giveMarkers(player.getPlayerData().takeBettedMarkers(win));
        }

        boolean done = true;
        for (PokerPlayer player : players) {
            if (player.getPlayerData().getBettedMarkers() > 0) {
                done = false;
            }
        }
        
        if (done) {
            sendGameInfo();
            for (int i = players.size() - 1; i >= 0; i--) {
                PokerPlayer player = players.get(i);
                if (player.getPlayerData().getMarkers() == 0) {
                    players.remove(player);
                }
            }
        } else {
            sendGameInfo();
            determineWinner();
        }
    }

    private String[] toPokerState(Connection connection) {
        String playercount = String.valueOf(players.size());
        String cardcount = String.valueOf(communityCards.size());

        Stack<String> arguments = new Stack<String>();

        arguments.push(playercount);
        for (PokerPlayer player : players) {
            String name = player.getName();
            String markers = String.valueOf(player.getPlayerData().getMarkers());
            String bettedMarkers = String.valueOf(player.getPlayerData().getBettedMarkers());
            String blind = player.getPlayerData().getBlind();
            String folded = String.valueOf(player.getPlayerData().hasFolded());
            String yourturn = String.valueOf(player == next);
            boolean isYou = player.getConnection() == connection;
            String you = String.valueOf(isYou);
            String card1;
            String card2;
            if (isYou || connection.getType() == Connection.Type.SPECTATOR) {
                card1 = player.getPlayerData().getHand().get(0).toCode();
                card2 = player.getPlayerData().getHand().get(1).toCode();
            } else {
                card1 = "none";
                card2 = "none";
            }
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
        for (Card card : communityCards) {
            String cardcode = card.toCode();
            arguments.push(cardcode);
        }

        arguments.push(Integer.toString(smallBlind));
        arguments.push(Integer.toString(minBet));

        return arguments.toArray(String[]::new);
    }

    private void sendGameInfo() {
        for (Connection connection : server.getConnections()) {
            String[] arguments = toPokerState(connection);
            Protocol.sendPackage(Protocol.Command.SEND_POKERSTATE, arguments, connection);
        }
    }
}
