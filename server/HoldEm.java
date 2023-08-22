package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import comms.Broadcaster;
import comms.Connection;
import comms.Protocol;
import poker.Card;
import poker.CardCollection;
import poker.Deck;
import poker.HandRank;
import poker.HoldEmModel;

public class HoldEm {
    private PokerServer server;
    private Broadcaster updateSender;

    private List<PokerPlayer> players;
    private PokerPlayer toPlay;

    private Deck deck;
    private CardCollection communityCards;
    private CardCollection discardPile;

    private int smallBlind;
    private int minBet;

    private int smallBlindIndex;

    private String message;

    public HoldEm(PokerServer server) {
        this.server = server;
        this.updateSender = new Broadcaster();
        this.players = Collections.synchronizedList(new ArrayList<PokerPlayer>());
        this.smallBlindIndex = 0;
    }

    public void setup() {
        this.setupBlinds();
    }

    public void play() {
        while (true) {
            playRound();
            if (this.players.size() <= 1) {
                break;
            }
        }
    }

    public HoldEmModel getHoldEmModel() {
        return new HoldEmModel(toPokerState(null, true));
    }

    public String getMessage() {
        return this.message;
    }

    public void addPlayer(Connection connection) {
        this.players.add(new PokerPlayer(connection, 1000));
    }

    public int getPlayerCount() {
        return this.players.size();
    }

    public Broadcaster getUpdateSender() {
        return this.updateSender;
    }

    public List<PokerPlayer> getPlayers() {
        return this.players;
    }

    private void playRound() {
        start();
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

        this.smallBlind = 50;
        this.minBet = smallBlind * 2;

        resetPlayers();
        deal();
        setupBlinds();
    }

    private void deal() {
        for (PokerPlayer player : players) {
            player.getPlayerData().getHand().add(deck.draw());
            player.getPlayerData().getHand().add(deck.draw());
        }
    }

    private void setupBlinds() {
        int playerCount = players.size();

        this.smallBlindIndex++;
        this.smallBlindIndex %= playerCount;

        for (int i = 0; i < playerCount; i++) {
            PokerPlayer player = players.get(i);
            if (i == smallBlindIndex) {
                player.getPlayerData().setBlind("small");
            } else if (i == (smallBlindIndex + 1) % playerCount) {
                player.getPlayerData().setBlind("big");
            } else {
                player.getPlayerData().setBlind("none");
            }
        }
    }

    private void resetPlayers() {
        for (PokerPlayer player : players) {
            player.getPlayerData().getHand().clear();
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
        int choices = 0;
        for (PokerPlayer player : players) {
            if (!player.getPlayerData().hasFolded()) {
                if (player.getPlayerData().getMarkers() > 0) {
                    choices++;
                }
            }
        }
        if (choices < 2) {
            return;
        }

        int betsRemaining = players.size();
        int playerCount = players.size();
        int i = (smallBlindIndex + 2) % playerCount;
        while (betsRemaining > 0) {
            PokerPlayer player = players.get(i % playerCount);
            betsRemaining--;
            i++;
            this.toPlay = player;

            if (choices < 2) {
                return;
            }

            if (player.getPlayerData().hasFolded() || player.getPlayerData().getMarkers() == 0) {
                continue;
            }

            sendGameInfo(player.getName() + " to play.", false);

            Protocol.sendPackage(Protocol.Command.REQUEST_MOVE, new String[] {}, player.getConnection());
            Protocol.Command command = Protocol.readCommand(player.getConnection());

            if (command == Protocol.Command.SEND_MOVE) {
                String[] arguments = Protocol.readArguments(command, player.getConnection());
                String move = arguments[0];
                int value;
                try {
                    value = Integer.parseInt(arguments[1]);
                } catch (NumberFormatException e) {
                    value = 0;
                }
                value = Math.max(0, value);

                int remaining = this.minBet - player.getPlayerData().getBettedMarkers();
                switch (move) {
                    case "match":
                        player.getPlayerData().bet(remaining);
                        player.getPlayerStatistics().addMatch();
                        Protocol.sendPackage(Protocol.Command.ACCEPTED_MOVE, new String[] {}, player.getConnection());
                        break;
                    case "check": {
                        if (remaining == 0) {
                            player.getPlayerStatistics().addCheck();
                            Protocol.sendPackage(Protocol.Command.ACCEPTED_MOVE, new String[] {},
                                    player.getConnection());
                        } else {
                            player.getPlayerData().setFolded(true);
                            choices--;
                            player.getPlayerStatistics().addFold();
                            Protocol.sendPackage(Protocol.Command.ACCEPTED_MOVE, new String[] {},
                                    player.getConnection());
                        }
                        break;
                    }
                    case "raise": {
                        int n = player.getPlayerData().bet(value + remaining);
                        if (n > remaining) {
                            this.minBet += n - remaining;
                            betsRemaining = (playerCount - 1);
                            player.getPlayerStatistics().addRaise(n - remaining);
                        } else {
                            player.getPlayerStatistics().addMatch();
                        }
                        Protocol.sendPackage(Protocol.Command.ACCEPTED_MOVE, new String[] {}, player.getConnection());
                        break;
                    }
                    case "fold": {
                        player.getPlayerData().setFolded(true);
                        choices--;
                        player.getPlayerStatistics().addFold();
                        Protocol.sendPackage(Protocol.Command.ACCEPTED_MOVE, new String[] {}, player.getConnection());
                        break;
                    }
                    default:
                        player.getPlayerData().setFolded(true);
                        choices--;
                        player.getPlayerStatistics().addFold();
                        Protocol.sendPackage(Protocol.Command.DENIED_MOVE, new String[] { "Unknown move: " + move },
                                player.getConnection());
                        break;
                }
            } else {
                player.getPlayerData().setFolded(true);
                Protocol.sendPackage(Protocol.Command.DENIED_MOVE, new String[] { "Unknown command" },
                        player.getConnection());
            }
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
        ArrayList<PokerPlayer> winners = new ArrayList<PokerPlayer>();
        HandRank maxRank = new HandRank();
        for (PokerPlayer player : players) {
            if (player.getPlayerData().hasFolded() || player.getPlayerData().getBettedMarkers() == 0) {
                continue;
            }
            CardCollection hand = CardCollection.join(communityCards, player.getPlayerData().getHand());
            HandRank rank = HandRank.rank(hand);
            int comparison = rank.compare(maxRank);
            if (comparison == 1) {
                winners.clear();
                winners.add(player);
                maxRank = rank;
            } else if (comparison == 0) {
                winners.add(player);
            }
        }
        int min = Integer.MAX_VALUE;
        for (PokerPlayer winner : winners) {
            if (winner.getPlayerData().getBettedMarkers() < min) {
                min = winner.getPlayerData().getBettedMarkers();
            }
        }

        int pot = 0;
        for (PokerPlayer player : players) {
            pot += player.getPlayerData().takeBettedMarkers(min);
        }

        for (PokerPlayer winner : winners) {
            winner.getPlayerData().giveMarkers(pot / winners.size());
        }

        boolean done = true;
        for (PokerPlayer player : players) {
            if (player.getPlayerData().getBettedMarkers() > 0) {
                done = false;
            }
        }

        this.toPlay = winners.get(0);
        StringBuilder winnerNames = new StringBuilder();
        for (PokerPlayer winner : winners) {
            winnerNames.append(winner.getName());
            winnerNames.append(" ");
        }
        sendGameInfo(winnerNames + "won!", true);
        if (done) {
            for (int i = players.size() - 1; i >= 0; i--) {
                PokerPlayer player = players.get(i);
                if (player.getPlayerData().getMarkers() == 0) {
                    players.remove(player);
                }
            }
        } else {
            determineWinner();
        }
    }

    private String[] toPokerState(Connection connection, boolean show) {
        boolean showAll;
        if (show) {
            showAll = true;
        } else if (connection != null) {
            if (connection.getType() == Connection.Type.SPECTATOR) {
                showAll = true;
            } else {
                showAll = false;
            }
        } else {
            showAll = false;
        }

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
            String yourturn = String.valueOf(player == toPlay);
            boolean isYou = player.getConnection() == connection;
            String you = String.valueOf(isYou);
            String card1;
            String card2;
            if (isYou || showAll) {
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

    private void sendGameInfo(String message, boolean show) {
        String[] arguments;
        for (Connection connection : server.getConnections()) {
            arguments = toPokerState(connection, show);
            Protocol.sendPackage(Protocol.Command.SEND_POKERSTATE, arguments, connection);
            arguments = new String[] { message };
            Protocol.sendPackage(Protocol.Command.SEND_MESSAGE, arguments, connection);
        }
        this.message = message;
        updateSender.broadcast();

        requestContinue();
    }

    private void requestContinue() {
        for (int i = 0; i < server.getConnections().size(); i++) {
            Connection connection = server.getConnections().get(i);
            Protocol.sendPackage(Protocol.Command.REQUEST_CONTINUE, new String[] {}, connection);
            Protocol.Command command = Protocol.readCommand(connection);
            if (command == Protocol.Command.SEND_CONTINUE) {
                continue;
            } else {
                i--;
            }
        }
    }
}