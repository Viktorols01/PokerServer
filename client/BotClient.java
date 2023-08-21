package client;

import poker.HoldEmModel;

public class BotClient extends PokerClient {

    String name;
    PokerBot bot;

    public BotClient(String name, PokerBot bot, boolean verbose) {
        super(verbose);
        this.name = name;
        this.bot = bot;
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
        return this.bot.getMove(model);
    }

    @Override
    protected String[] getContinue() {
        return new String[] {};
    }

    @Override
    protected void display(HoldEmModel model) {
    }

    @Override
    protected void parseMessage(String message) {
    }
}
