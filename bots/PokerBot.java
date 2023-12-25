package bots;

import poker.HoldEmModel;

public abstract class PokerBot {

    public abstract String[] getMove(HoldEmModel model);

    protected static final String[] match() {
        return new String[] { "match", "0" };
    }
    
    protected static final String[] check() {
        return new String[] { "check", "0" };
    }

    protected static final String[] raise(int n) {
        return new String[] { "raise", String.valueOf(n) };
    }

    protected static final String[] fold() {
        return new String[] { "fold", "0" };
    }
}
