package main;

import client.PokerClient;

public class ClientRunner {
    public static void main(String[] args) {
        PokerClient client = SecretClientGetter.getPokerClient("you", false);
        client.connect("localhost", 50160);
        client.start();
    }
}