package client;

import frames.SpectatorFrame;
import poker.HoldEmModel;

public class SpectatorClient extends PokerClient {

    String name;
    SpectatorFrame frame = new SpectatorFrame(1200, 800);

    public SpectatorClient(String name, boolean verbose) {
        super(verbose);
        this.name = name;
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
        return new String[] { "spectator" };
    }

    @Override
    protected String[] getMove(HoldEmModel model) {
        return new String[] { "fold", "0" };
    }

    @Override
    protected String[] getContinue() {
        return frame.getContinue();
    }

    @Override
    protected void display(HoldEmModel model) {
        frame.updateModel(model);
    }

    @Override
    protected void parseMessage(String message) {
        frame.updateMessage(message);
    }
}