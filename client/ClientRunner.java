package client;

public class ClientRunner {
    public static void main(String[] args) {
        Client client = new Client(false);
        client.connect("localhost", 50160);
        client.start();
    }
}