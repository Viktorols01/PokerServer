package test;

import poker.Card;
import poker.CardCollection;
import poker.HandRank;

public class RankTest {
    public static void main(String[] args) {
        CardCollection hand = new CardCollection();
        hand.add(new Card(Card.Color.HEARTS, 13));
        hand.add(new Card(Card.Color.HEARTS, 12));
        hand.add(new Card(Card.Color.HEARTS, 11));
        hand.add(new Card(Card.Color.HEARTS, 10));
        hand.add(new Card(Card.Color.SPADES, 11));

        hand.add(new Card(Card.Color.HEARTS, 1));
        hand.add(new Card(Card.Color.CLUBS, 11));
        HandRank rank = HandRank.rank(hand);
        System.out.println(rank);
    }
}
