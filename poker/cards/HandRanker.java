package poker.cards;

// High card: 1
// One pair: 2
// Two pair: 3
// Three of a kind: 4
// Straight: 5
// Flush: 6
// Full house: 7
// Four of a kind: 8
// Straight flush: 9
// Royal flush: 10

public class HandRanker {
    public static int rank(CardCollection hand) {
        if (hasRoyalFlush()) {
            return 10;
        } else if (hasStraightFlush()) {
            return 9;
        } else if (hasFourOfAKind()) {
            return 8; 
        } else if (hasFullHouse()) {
            return 7;
        } else if (hasFlush()) {
            return 6;
        } else if (hasStraight()) {
            return 5;
        } else if (hasThreeOfAKind()) {
            return 4;
        } else if (hasTwoPair()) {
            return 3;
        } else if (hasPair()) {
            return 2;
        } else {
            return 1;
        }
    }

    private static boolean hasRoyalFlush() {
        return false;
    }

    private static boolean hasStraightFlush() {
        return false;
    }

    private static boolean hasFourOfAKind() {
        return false;
    }

    private static boolean hasFullHouse() {
        return false;
    }

    private static boolean hasFlush() {
        return false;
    }

    private static boolean hasStraight() {
        return false;
    }

    private static boolean hasThreeOfAKind() {
        return false;
    }

    private static boolean hasTwoPair() {
        return false;
    }

    private static boolean hasPair() {
        return false;
    }
}
