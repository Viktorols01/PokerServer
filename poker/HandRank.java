package poker;

public class HandRank {
    private int value;
    private CardCollection cards;

    private final static int MAXCARDS = 5;

    public HandRank() {
        this.value = 0;
        this.cards = null;
    }

    public HandRank(int value, int kickerValue) {
        this.value = value;
        this.cards = new CardCollection();
        this.cards.add(new Card(null, kickerValue));
    }

    public HandRank(int value) {
        this.value = value;
        this.cards = null;
    }

    private HandRank(int value, CardCollection cards) {
        this.value = value;
        this.cards = cards;
    }

    public int compare(HandRank rank) {
        if (this.value > rank.value) {
            return 1;
        } else if (this.value < rank.value) {
            return -1;
        } else {
            if (this.cards == null) {
                return -1;
            } else if (rank.cards == null) {
                return 1;
            }
            for (int i = 0; i < MAXCARDS; i++) {
                if (this.cards.size() <= i) {
                    return -1;
                } else if (rank.cards.size() <= i) {
                    return 1;
                }
                if (this.cards.get(i).getValue() > rank.cards.get(i).getValue()) {
                    return 1;
                } else if (this.cards.get(i).getValue() < rank.cards.get(i).getValue()) {
                    return -1;
                }
            }
        }
        return 0;
    }

    public static HandRank rank(CardCollection cardCollection) {
        CardGrid grid = getGrid(cardCollection);

        int i = 9;
        HandRank rank = null;
        while (rank == null) {
            switch (i) {
                case 9:
                    rank = getStraightFlush(grid);
                    break;
                case 8:
                    rank = getFourOfAKind(grid);
                    break;
                case 7:
                    rank = getFullHouse(grid);
                    break;
                case 6:
                    rank = getFlush(grid);
                    break;
                case 5:
                    rank = getStraight(grid);
                    break;
                case 4:
                    rank = getThreeOfAKind(grid);
                    break;
                case 3:
                    rank = getTwoPair(grid);
                    break;
                case 2:
                    rank = getPair(grid);
                    break;
                case 1:
                    rank = getHighCard(grid);
                    break;
                case 0:
                    rank = new HandRank(0);
                    break;
            }
            i--;
        }
        return rank;
    }

    private static HandRank getHighCard(CardGrid grid) {
        CardCollection cards = new CardCollection();
        for (int value = 14; value > 1; value--) {
            for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
                if (grid.hasCard(colorIndex, value)) {
                    cards.add(grid.getCard(colorIndex, value));
                    if (cards.size() >= MAXCARDS) {
                        return new HandRank(1, cards);
                    }
                }

            }
        }
        if (cards.size() > 0) {
            return new HandRank(1, cards);
        } else {
            return null;
        }
    }

    private static HandRank getPair(CardGrid grid) {
        CardCollection cards = new CardCollection();
        int found = 0;
        for (int value = 14; value > 1; value--) {
            CardCollection row = grid.getCardsOfValue(value);
            if (row.size() == 2) {
                for (Card card : row) {
                    cards.addWithMaxSize(card, 0, MAXCARDS);
                }
                found++;
            } else {
                for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
                    if (grid.hasCard(colorIndex, value)) {
                        cards.addWithMaxSize(grid.getCard(colorIndex, value), MAXCARDS);
                    }
                }
            }
            if (cards.size() >= MAXCARDS && found == 1) {
                return new HandRank(2, cards);
            }
            if (found == 1) {
                return new HandRank(2, cards);
            }
        }
        return null;
    }

    private static HandRank getTwoPair(CardGrid grid) {
        CardCollection cards = new CardCollection();
        int found = 0;
        for (int value = 14; value > 1; value--) {
            CardCollection row = grid.getCardsOfValue(value);
            if (row.size() == 2 && found < 2) {
                for (Card card : row) {
                    cards.addWithMaxSize(card, found * 2, MAXCARDS);
                }
                found++;
            } else {
                for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
                    if (grid.hasCard(colorIndex, value)) {
                        cards.addWithMaxSize(grid.getCard(colorIndex, value), MAXCARDS);
                    }
                }
            }
            if (cards.size() >= MAXCARDS && found == 2) {
                return new HandRank(3, cards);
            }
        }
        return null;
    }

    private static HandRank getThreeOfAKind(CardGrid grid) {
        CardCollection cards = new CardCollection();
        int found = 0;
        for (int value = 14; value > 1; value--) {
            CardCollection row = grid.getCardsOfValue(value);
            if (row.size() == 3) {
                for (Card card : row) {
                    cards.addWithMaxSize(card, 0, MAXCARDS);
                }
                found++;
            } else {
                for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
                    if (grid.hasCard(colorIndex, value)) {
                        cards.addWithMaxSize(grid.getCard(colorIndex, value), MAXCARDS);
                    }
                }
            }
            if (cards.size() >= MAXCARDS && found == 1) {
                return new HandRank(4, cards);
            }
            if (found == 1) {
                return new HandRank(4, cards);
            }
        }
        return null;
    }

    private static HandRank getStraight(CardGrid grid) {
        CardCollection cards = new CardCollection();
        for (int value = 14; value > 0; value--) {
            CardCollection row = grid.getCardsOfValue(value);
            if (row.size() > 0) {
                cards.add(row.get(0));
            } else {
                cards.clear();
            }

            if (cards.size() >= MAXCARDS) {
                return new HandRank(5, cards);
            }
        }
        return null;
    }

    private static HandRank getFlush(CardGrid grid) {
        CardCollection cards = new CardCollection();
        for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
            cards.clear();
            for (int value = 14; value > 1; value--) {
                if (grid.hasCard(colorIndex, value)) {
                    cards.add(grid.getCard(colorIndex, value));
                }
                if (cards.size() >= MAXCARDS) {
                    return new HandRank(6, cards);
                }
            }
        }
        return null;
    }

    private static HandRank getFullHouse(CardGrid grid) {
        CardCollection cards = new CardCollection();
        int found = 0;
        for (int value = 14; value > 1; value--) {
            CardCollection row = grid.getCardsOfValue(value);
            if (row.size() == 3 && found == 0) {
                for (Card card : row) {
                    cards.addWithMaxSize(card, 0, MAXCARDS);
                }
                found++;
            } else if (row.size() >= 2) {
                for (Card card : row) {
                    cards.addWithMaxSize(card, 5);
                }
            }
            if (cards.size() >= MAXCARDS && found == 1) {
                return new HandRank(7, cards);
            }
        }
        return null;
    }

    private static HandRank getFourOfAKind(CardGrid grid) {
        CardCollection cards = new CardCollection();
        int found = 0;
        for (int value = 14; value > 1; value--) {
            CardCollection row = grid.getCardsOfValue(value);
            if (row.size() == 4) {
                for (Card card : row) {
                    cards.addWithMaxSize(card, 0, MAXCARDS);
                }
                found++;
            } else {
                for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
                    if (grid.hasCard(colorIndex, value)) {
                        cards.addWithMaxSize(grid.getCard(colorIndex, value), 5);
                    }

                }
            }
            if (cards.size() >= MAXCARDS && found == 1) {
                return new HandRank(8, cards);
            }
        }
        return null;
    }

    private static HandRank getStraightFlush(CardGrid grid) {
        CardCollection cards = new CardCollection();
        for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
            for (int value = 14; value > 0; value--) {
                if (grid.hasCard(colorIndex, value)) {
                    cards.add(grid.getCard(colorIndex, value));
                } else {
                    cards.clear();
                }
                if (cards.size() >= MAXCARDS) {
                    return new HandRank(9, cards);
                }
            }
        }
        return null;
    }

    private static class CardGrid {
        private boolean[][] grid = new boolean[4][14];

        public CardGrid() {
            this.grid = new boolean[4][14];
        }

        private void addCard(Card card) {
            if (card == null) {
                return;
            }
            int value = card.getValue();
            int colorIndex = card.getColor().ordinal();
            if (value == 1) {
                this.grid[colorIndex][13] = true;
            }
            this.grid[colorIndex][value - 1] = true;
        }

        private Card getCard(int colorIndex, int value) {
            return new Card(Card.Color.values()[colorIndex], value);
        }

        private boolean hasCard(int colorIndex, int value) {
            return this.grid[colorIndex][value - 1];
        }

        private CardCollection getCardsOfValue(int value) {
            CardCollection cards = new CardCollection();
            for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
                if (hasCard(colorIndex, value)) {
                    cards.add(getCard(colorIndex, value));
                }
            }
            return cards;
        }
    }

    private static CardGrid getGrid(CardCollection cardCollection) {
        CardGrid grid = new CardGrid();
        for (Card card : cardCollection) {
            grid.addCard(card);
        }
        return grid;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        switch (this.value) {
            case 0:
                str.append("Unknown");
                break;
            case 1:
                str.append("High card");
                break;
            case 2:
                str.append("Pairs");
                break;
            case 3:
                str.append("Two pairs");
                break;
            case 4:
                str.append("Three of a kind");
                break;
            case 5:
                str.append("Straight");
                break;
            case 6:
                str.append("Flush");
                break;
            case 7:
                str.append("Full house");
                break;
            case 8:
                str.append("Four of a kind");
                break;
            case 9:
                str.append("Straight flush");
                break;
            case 10:
                str.append("Royal flush");
                break;
        }
        if (cards != null) {
            if (cards.size() > 0) {
                str.append(" ");
                str.append(cards.get(0).getValueString());
            }
        }
        return str.toString();
    }
}