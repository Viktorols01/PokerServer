package main;

import client.PokerBot;
import poker.HoldEmModel;

public class MyBot extends PokerBot {

    @Override
    public String[] getMove(HoldEmModel model) {
        return fold();
    }
    
}
