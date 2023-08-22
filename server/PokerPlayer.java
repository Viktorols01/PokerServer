package server;

import comms.Connection;
import poker.PlayerData;
import poker.PlayerStatistics;

public class PokerPlayer {
    private Connection connection;
    private PlayerData data;    
    private PlayerStatistics statistics;

    public PokerPlayer(Connection connection, int markers) {
        this.connection = connection;
        this.data = new PlayerData(connection.getName(), markers);
        this.statistics = new PlayerStatistics();
    }

    public String getName() {
        return this.connection.getName();
    }

    public PlayerData getPlayerData() {
        return this.data;
    }

    public PlayerStatistics getPlayerStatistics() {
        return this.statistics;
    }

    public String getIP() {
        return this.connection.getSocket().getInetAddress().getHostAddress();
    }

    public Connection getConnection() {
        return this.connection;
    }
}
