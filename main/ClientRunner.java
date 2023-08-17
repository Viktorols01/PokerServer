package main;

import client.PokerClient;
import frames.PlayerFrame;
import poker.HoldEmModel;

public class ClientRunner {
    public static void main(String[] args) {
        PokerClient client = new MyClient(false);
        client.connect("localhost", 50160);
        client.start();
    }

    private static class MyClient extends PokerClient {

        PlayerFrame pokerframe;

        public MyClient(boolean verbose) {
            super(verbose);
            this.pokerframe = new PlayerFrame(1000, 600);
        }

        @Override
        protected void setup() {
        }

        @Override
        protected String[] getName() {
            return new String[] { "Your name" };
        }

        @Override
        protected String[] getType() {
            return new String[] { "player" };
        }

        @Override
        protected String[] getMove(HoldEmModel model) {
            return pokerframe.getMove();
        }

        @Override
        protected void display(HoldEmModel model) {
            pokerframe.updateModel(model);
        }

        @Override
        protected void parseMessage(String message) {
            pokerframe.updateMessage(message);
        }

    }
}
