package poker;

public class HandRank {
    private int value;
    private int kickerValue;

    public HandRank(int value, int kickerValue) {
        this.value = value;
        this.kickerValue = kickerValue;
    }

    public HandRank() {
        this.value = 0;
        this.kickerValue = 0;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getKickerValue() {
        return kickerValue;
    }

    public void setKickerValue(int kickerValue) {
        this.kickerValue = kickerValue;
    }

    public boolean greaterThan(HandRank rank) {
        if (this.value == rank.getValue()) {
            return this.kickerValue > rank.getKickerValue();
        } else {
            return this.value > rank.getValue();
        }
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
            }
            i--;
        }
        return rank;
    }

    private static HandRank getHighCard(CardGrid grid) {
        int maxValue = 0;
        for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
            for (int value = 14; value > 0; value--) {
                if (grid.hasCard(colorIndex, value)) {
                    if (value > maxValue) {
                        maxValue = value;
                    }
                }
            }
        }
        return new HandRank(1, maxValue);
    }

    private static HandRank getPair(CardGrid grid) {
        for (int value = 14; value > 0; value--) {
            if (grid.valueCount(value) == 2) {
                return new HandRank(2, value);
            }
        }
        return null;
    }

    private static HandRank getTwoPair(CardGrid grid) {
        int pairCounter = 0;
        int maxValue = 0;
        for (int value = 14; value > 0; value--) {
            if (grid.valueCount(value) == 2) {
                if (value > maxValue) {
                    maxValue = value;
                }
                pairCounter++;
            }
            if (pairCounter == 2) {
                return new HandRank(3, maxValue);
            }
        }
        return null;
    }

    private static HandRank getThreeOfAKind(CardGrid grid) {
        for (int value = 14; value > 0; value--) {
            if (grid.valueCount(value) == 3) {
                return new HandRank(4, value);
            }
        }
        return null;
    }

    private static HandRank getStraight(CardGrid grid) {
        int startValue = 14;
        int counter = 0;
        for (int value = 14; value > 0; value--) {
            if (grid.valueCount(value) > 0) {
                counter++;
            } else {
                startValue = value - 1;
                counter = 0;
            }

            if (counter == 5) {
                return new HandRank(5, startValue);
            }
        }
        return null;
    }

    private static HandRank getFlush(CardGrid grid) {
        int maxValue = 0;
        for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
            int counter = 0;
            for (int value = 14; value > 1; value--) {
                if (grid.hasCard(colorIndex, value)) {
                    if (value > maxValue) {
                        maxValue = value;
                    }
                    counter++;
                }
                if (counter == 5) {
                    return new HandRank(6, maxValue);
                }
            }
        }
        return null;
    }

    private static HandRank getFullHouse(CardGrid grid) {
        int tripleValue = 0;
        int doubleValue = 0;
        for (int value = 14; value > 0; value--) {
            if (grid.valueCount(value) == 3) {
                if (value > tripleValue) {
                    tripleValue = value;
                }
            } else if (grid.valueCount(value) == 2) {
                if (value > doubleValue) {
                    doubleValue = value;
                }
            }
            if (tripleValue > 0 && doubleValue > 0) {
                return new HandRank(7, tripleValue);
            }
        }
        return null;
    }

    private static HandRank getFourOfAKind(CardGrid grid) {
        for (int value = 14; value > 0; value--) {
            if (grid.valueCount(value) == 4) {
                return new HandRank(8, value);
            }
        }
        return null;
    }

    private static HandRank getStraightFlush(CardGrid grid) {
        int startValue = 14;
        int counter = 0;
        for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
            counter = 0;
            for (int value = 14; value > 0; value--) {
                if (grid.hasCard(colorIndex, value)) {
                    counter++;
                } else {
                    startValue = value - 1;
                    counter = 0;
                }

                if (counter == 5) {
                    return new HandRank(9, startValue);
                }
            }
        }
        return null;
    }

    private static CardGrid getGrid(CardCollection cardCollection) {
        CardGrid grid = new CardGrid();
        for (Card card : cardCollection) {
            grid.setCard(card);
        }
        return grid;
    }

    private static class CardGrid {
        private boolean[][] grid = new boolean[4][14];

        public CardGrid() {
            this.grid = new boolean[4][14];
        }

        private void setCard(Card card) {
            int value = card.getValue();
            int colorIndex = card.getColor().ordinal();
            if (value == 1) {
                this.grid[colorIndex][13] = true;
            }
            this.grid[colorIndex][value - 1] = true;
        }

        private boolean hasCard(int colorIndex, int value) {
            return this.grid[colorIndex][value - 1];
        }

        private int valueCount(int value) {
            int counter = 0;
            for (int colorIndex = 0; colorIndex < 4; colorIndex++) {
                if (hasCard(colorIndex, value)) {
                    counter++;
                }
            }
            return counter;
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        switch (this.value) {
            case 1:
                str.append("High card " + this.kickerValue);
                break;
            case 2:
                str.append("Pairs " + this.kickerValue);
                break;
            case 3:
                str.append("Two pairs " + this.kickerValue);
                break;
            case 4:
                str.append("Three of a kind " + this.kickerValue);
                break;
            case 5:
                str.append("Straight " + this.kickerValue);
                break;
            case 6:
                str.append("Flush " + this.kickerValue);
                break;
            case 7:
                str.append("Full house " + this.kickerValue);
                break;
            case 8:
                str.append("Four of a kind " + this.kickerValue);
                break;
            case 9:
                str.append("Straight flush " + this.kickerValue);
                break;
            case 10:
                str.append("Royal flush " + this.kickerValue);
                break;
        }
        return str.toString();
    }
}
