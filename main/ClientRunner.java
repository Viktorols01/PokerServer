package main;

import client.PokerClient;
import client.PokerTerminal;
import poker.PokerModel;

public class ClientRunner {
    public static void main(String[] args) {
        PokerClient client = new MyClient(false);
        client.connect("localhost", 50160);
        client.start();
    }

    private static class MyClient extends PokerClient {

        public MyClient(boolean verbose) {
            super(verbose);
        }

        @Override
        protected void setup() {
        }

        @Override
        protected String[] getName() {
            return new String[] { "name" };
        }

        @Override
        protected String[] getType() {
            return new String[] { "player" };
        }

        @Override
        protected String[] getMove(PokerModel model) {
            return new String[] { "fold", "0" };
        }

        @Override
        protected void display(PokerModel model) {
            PokerTerminal.printModel(model);
        }

        @Override
        protected void parseMessage(String message) {
            System.out.println(message);
        }

    }
}
