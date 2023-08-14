package client;

public class ClientRunner {
    public static void main(String[] args) {
        //Client botClient = new Client(true, false);
        //botClient.connect("localhost", 50160);
        //botClient.start();

        Client client = new Client(false, true);
        client.connect("localhost", 50160);
        client.start();
    }
}