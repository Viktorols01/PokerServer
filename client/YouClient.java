package client;

import frames.PlayerFrame;
import poker.HoldEmModel;

public class YouClient extends PokerClient {

    String name;
    PlayerFrame pokerframe;

    public YouClient(String name, boolean verbose) {
        super(verbose);
        this.name = name;
        this.pokerframe = new PlayerFrame(1200, 800);
    }

    @Override
    protected void setup() {
    }

    @Override
    protected String[] getName() {
        return new String[] { name };
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
    protected String[] getContinue() {
        return new String[] {};
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