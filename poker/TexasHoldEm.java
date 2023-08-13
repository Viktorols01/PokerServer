package poker;

import java.util.List;
import java.util.Stack;

import comms.Connection;
import comms.Protocol;
import poker.cards.Card;
import poker.cards.CardCollection;
import poker.cards.Deck;

public class TexasHoldEm {
    private PokerServer server;
    private List<PokerPlayer> players;
    private PokerPlayer next;

    private Deck deck;
    private CardCollection communityCards;
    private CardCollection discardPile;

    private int pot;
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
        this.deck.shuffle(0);
        this.communityCards = new CardCollection();
        this.discardPile = new CardCollection();

        this.pot = 0;
        this.smallBlind = 50;
        this.minBet = smallBlind * 2;
    }

    private void deal() {
        for (PokerPlayer player : players) {
            player.getPlayerData().getHand().add(deck.draw());
            player.getPlayerData().getHand().add(deck.draw());
        }
        sendGameInfo();
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
        sendGameInfo();
    }

    private void getBets() {
        for (PokerPlayer player : players) {
            this.next = player;
            sendGameInfo();
            if (player.getPlayerData().hasFolded()) {
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
                        if (remaining == 0) {
                            Protocol.sendPackage(Protocol.Command.ACCEPTED, new String[] {}, player.getConnection());
                        }
                        if (player.getPlayerData().getMarkers() >= remaining) {
                            player.getPlayerData().bet(remaining);
                            Protocol.sendPackage(Protocol.Command.ACCEPTED, new String[] {}, player.getConnection());
                        } else {
                            player.getPlayerData().setFolded(true);
                            Protocol.sendPackage(Protocol.Command.DENIED, new String[] { "Can't afford checking" },
                                    player.getConnection());
                        }
                        break;
                    }
                    case "raise": {
                        int remaining = this.minBet - player.getPlayerData().getBettedMarkers();
                        if (value + remaining <= player.getPlayerData().getMarkers()) {
                            player.getPlayerData().bet(value + remaining);
                            this.minBet += value;
                            Protocol.sendPackage(Protocol.Command.ACCEPTED, new String[] {}, player.getConnection());
                        } else {
                            player.getPlayerData().setFolded(true);
                            Protocol.sendPackage(Protocol.Command.DENIED, new String[] { "Can't afford raising" },
                                    player.getConnection());
                        }
                        break;
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
        }
        boolean done = true;
        for (PokerPlayer player : players) {
            if (player.getPlayerData().getBettedMarkers() != this.minBet && !player.getPlayerData().hasFolded()) {
                done = false;
            }
        }
        if (done) {
            for (PokerPlayer player : players) {
                this.pot += player.getPlayerData().emptyBettedMarkers();
            }
            this.minBet = 0;
            sendGameInfo();
        } else {
            getBets();
        }
    }

    private void flop() {
        this.discardPile.add(deck.draw());
        this.communityCards.add(deck.draw());
        this.communityCards.add(deck.draw());
        this.communityCards.add(deck.draw());
        sendGameInfo();
    }

    private void turn() {
        this.discardPile.add(deck.draw());
        this.communityCards.add(deck.draw());
        sendGameInfo();
    }

    private void river() {
        this.discardPile.add(deck.draw());
        this.communityCards.add(deck.draw());
        sendGameInfo();
    }

    private void determineWinner() {
        sendGameInfo();
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

        arguments.push(Integer.toString(pot));
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
