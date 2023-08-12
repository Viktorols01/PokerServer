package poker;

import java.util.ArrayList;

public class CardCollection {
    protected ArrayList<Card> cards;

    public CardCollection() {
        this.cards = new ArrayList<Card>();
    }

    @Override
    public String toString() {
        StringBuilder acc = new StringBuilder();
        for (Card card : cards) {
            if (!acc.isEmpty()) {
                acc.append('\n');
            }
            acc.append(card.toString());
        }
        return acc.toString();
    }

    public void add(Card card) {
        cards.add(card);
    }

    public int size() {
        return cards.size();
    }
}
