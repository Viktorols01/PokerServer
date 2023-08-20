package main;

import frames.ServerFrame;
import server.PokerServer;

public class ServerRunner {
    public static void main(String[] args) {
        PokerServer server = new PokerServer(50160);
        new ServerFrame(server, 1200, 800);
    }
}