package comms;

public class Client {
    private String name;
    private Type type;
    private Connection connection;
    
    public Client(String name, Type type, Connection connection) {
        this.name = name;
        this.type = type;
        this.connection = connection;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Connection getConnection() {
        return connection;
    }

    public enum Type {
        PLAYER,
        SPECTATOR;
    }
}
