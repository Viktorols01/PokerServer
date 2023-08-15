package client;

import poker.Card;
import poker.CardCollection;
import poker.HandRank;
import poker.PlayerData;
import poker.PokerModel;

public class PokerTerminal {
    public static void printModel(PokerModel model) {
        StringBuilder str = new StringBuilder();
        str.append("---");
        str.append("\n");
        str.append("PokerState: ");
        str.append("\n");
        str.append("Name: " + model.getYou().getName());
        str.append("\n");
        str.append("Players: ");
        for (PlayerData player : model.getPlayers()) {
            str.append("\n\t");
            if (player.hasFolded()) {
                str.append("\u001b[2m");
            }
            if (player == model.getToPlay()) {
                str.append("\u001b[32m");
            }
            if (player == model.getYou()) {
                str.append("You" + ": " + player.getMarkers() + ":" + player.getBettedMarkers() + " ("
                        + player.getBlind() + " blind)");
            } else {
                str.append(player.getName() + ": " + player.getMarkers() + ":" + player.getBettedMarkers() + " ("
                        + player.getBlind() + " blind)");
            }
            str.append("\u001b[0m");
        }
        str.append("\n");
        str.append("Pot: " + model.getPot());
        str.append("\n");
        str.append("Community cards: ");
        for (Card card : model.getCommunityCards()) {
            str.append("\n\t");
            str.append(card);
        }
        str.append("\n");
        str.append("Your hand: "
                + HandRank.rank(CardCollection.join(model.getYou().getHand(), model.getCommunityCards())));
        for (Card card : model.getYou().getHand()) {
            str.append("\n\t");
            str.append(card);
        }
        str.append("\n");
        str.append("Need to bet: " + (model.getMinBet() - model.getYou().getBettedMarkers()));
        str.append("\n");
        str.append("Your markers: " + model.getYou().getMarkers() + " (" + model.getYou().getBettedMarkers() + ")");
        str.append("\n");
        str.append("---");
        System.out.println(str.toString());
    }
}
