package main;

import client.PokerClient;
import client.PokerFrame;
import poker.PokerModel;

public class ClientRunner {
    public static void main(String[] args) {
        PokerClient client = new MyClient(false);
        client.connect("localhost", 50160);
        client.start();
    }

    private static class MyClient extends PokerClient {

        PokerFrame pokerframe;

        public MyClient(boolean verbose) {
            super(verbose);
            this.pokerframe = new PokerFrame();
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
        protected String[] getMove(PokerModel model) {
            return pokerframe.getMove();
        }

        @Override
        protected void display(PokerModel model) {
            pokerframe.updateModel(model);
        }

        @Override
        protected void parseMessage(String message) {
            pokerframe.updateMessage(message);
        }

    }
}
