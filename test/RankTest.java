package test;

import poker.Card;
import poker.CardCollection;
import poker.HandRank;

public class RankTest {
    public static void main(String[] args) {
        CardCollection hand = new CardCollection();
        hand.add(new Card(Card.Color.CLUBS, 5));
        hand.add(new Card(Card.Color.CLUBS, 6));
        hand.add(new Card(Card.Color.DIAMONDS, 13));
        hand.add(new Card(Card.Color.DIAMONDS, 6));
        hand.add(new Card(Card.Color.SPADES, 4));

        hand.add(new Card(Card.Color.DIAMONDS, 5));
        hand.add(new Card(Card.Color.SPADES, 13));
        HandRank rank = HandRank.rank(hand);
        System.out.println(rank);
    }
}

/*
 bug (FIXAD):
hand.add(new Card(Card.Color.CLUBS, 5));
hand.add(new Card(Card.Color.CLUBS, 6));
hand.add(new Card(Card.Color.DIAMONDS, 13));
hand.add(new Card(Card.Color.DIAMONDS, 6));
hand.add(new Card(Card.Color.SPADES, 4));

hand.add(new Card(Card.Color.DIAMONDS, 5));
hand.add(new Card(Card.Color.SPADES, 13));

 */