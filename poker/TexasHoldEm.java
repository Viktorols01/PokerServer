package poker;

public class TexasHoldEm {
    private Deck deck;
    private CardCollection communityCards;
    private CardCollection discardPile;

    public TexasHoldEm() {
        this.deck = new Deck();
        this.deck.shuffle(0);
        this.communityCards = new CardCollection();
    }

    public void flop() {
        this.discardPile.add(deck.draw());
        this.communityCards.add(deck.draw());
        this.communityCards.add(deck.draw());
        this.communityCards.add(deck.draw());
    }

    public void turn() {
        this.discardPile.add(deck.draw());
        this.communityCards.add(deck.draw());
    }

    public void river() {
        this.discardPile.add(deck.draw());
        this.communityCards.add(deck.draw());
    }

    public CardCollection getCommunityCards() {
        return communityCards;
    }

}
