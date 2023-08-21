package client;

import poker.HoldEmModel;

public abstract class PokerBot {

    public abstract String[] getMove(HoldEmModel model);

    protected static String[] check() {
        return new String[] { "check", "0" };
    }

    protected static String[] raise(int n) {
        return new String[] { "raise", String.valueOf(n) };
    }

    protected static String[] fold() {
        return new String[] { "fold", "0" };
    }
}
