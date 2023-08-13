package poker.cards;

import java.util.Collections;
import java.util.Random;

public class Deck extends CardCollection {
    public Deck() {
        for (Card.Color color : Card.Color.values()) {
            for (int value = 1; value <= 13; value++) {
                this.cards.add(new Card(color, value));
            }
        }
    }

    public void shuffle(long seed) {
        Random random = new Random(seed);
        Collections.shuffle(cards, random);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card draw() {
        return this.cards.remove(cards.size() - 1);
    }
}