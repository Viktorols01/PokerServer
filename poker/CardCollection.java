package poker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CardCollection implements Iterable<Card>, Cloneable {
    protected List<Card> cards;

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

    public void pushFirstWithMaxSize(Card card, int maxSize) {
        cards.add(0, card);
        if (size() > maxSize) {
            cards.remove(size() - 1);
        }
    }

    public void addWithMaxSize(Card card, int maxSize) {
        if (size() < maxSize) {
            add(card);
        }
    }

    public Card get(int i) {
        return cards.get(i);
    }

    public boolean hasIndex(int i) {
        if (i < 0) {
            return false;
        } else if (i >= cards.size()) {
            return false;
        } else {
            return true;
        }
    }

    public int size() {
        return cards.size();
    }

    public void sortHighToLow() {
        this.cards.sort((Card c1, Card c2) -> {
            int d = c2.getValue() - c1.getValue();
            if (d == 0) {
                return c2.getColor().ordinal() - c1.getColor().ordinal();
            } else {
                return d;
            }
        });
    }

    public static CardCollection join(CardCollection c1, CardCollection c2) {
        CardCollection c = new CardCollection();
        for (Card card : c1) {
            c.add(card);
        }
        for (Card card : c2) {
            c.add(card);
        }
        return c;
    }

    @Override
    public Iterator<Card> iterator() {
        return cards.iterator();
    }

    @Override
    public CardCollection clone() {
        CardCollection clone = new CardCollection();
        for (Card c : this.cards) {
            clone.add(c);
        }
        return clone;
    }

    public void clear() {
        this.cards.clear();
    }
}
