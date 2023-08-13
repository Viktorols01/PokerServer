package poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import comms.Connection;
import comms.Protocol;
import comms.Protocol.Command;
import comms.Server;

public class PokerServer extends Server {
    private List<PokerPlayer> players;
    private TexasHoldEm game;
    private Thread gameThread;

    public PokerServer() {
        this.players = Collections.synchronizedList(new ArrayList<PokerPlayer>());
    }

    public void start() {
        this.game = new TexasHoldEm(this);
        this.gameThread = new Thread(() -> {
            game.round();
        }, "gameThread");
        this.gameThread.start();
    }

    @Override
    protected void readCommand(Connection connection, Command command) {
        switch (command) {
            case SEND_TYPE:
                if (connection.getType() == null) {
                    String[] arguments = Protocol.readArguments(Command.SEND_TYPE, connection);
                    Connection.Type type = Connection.Type.valueOf(arguments[0].toUpperCase());
                    switch (type) {
                        case PLAYER:
                            PokerPlayer player = new PokerPlayer(connection, 1000);
                            players.add(player);
                            setType(connection, type);
                            break;
                        case SPECTATOR:
                            setType(connection, type);
                            break;
                        default:
                            Protocol.sendPackage(Protocol.Command.DENIED, new String[] { "Invalid type" }, connection);
                            break;
                    }
                }
                break;
            default:
                Protocol.sendPackage(Protocol.Command.UNKNOWN_COMMAND, new String[0], connection);
                break;
        }
    }

    public List<PokerPlayer> getPlayers() {
        return this.players;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(super.toString());
        str.append("Players:" + "\n");
        for (PokerPlayer player : players) {
            str.append(player.getName() + ": " + player.getMarkers() + " markers \n");
        }
        return str.toString();
    }
}