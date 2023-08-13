package poker;

import comms.Connection;
import poker.cards.PlayerData;

public class PokerPlayer {
    private Connection connection;
    private PlayerData data;    

    public PokerPlayer(Connection connection, int markers) {
        this.connection = connection;
        this.data = new PlayerData(connection.getName(), markers);
    }

    public String getName() {
        return this.connection.getName();
    }

    public PlayerData getPlayerData() {
        return this.data;
    }

    public String getIP() {
        return this.connection.getSocket().getInetAddress().getHostAddress();
    }

    public Connection getConnection() {
        return this.connection;
    }
}
