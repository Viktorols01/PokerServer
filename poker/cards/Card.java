package poker.cards;

public class Card {
    private final Color color;
    private final int value;

    public Card(String code) {
        this.value = Integer.valueOf(code.substring(1));
        this.color = Color.decode(code.charAt(0));
    }

    public Card(Color color, int value) {
        this.color = color;
        this.value = value;
    }

    public Card(char c, int value) {
        this.color = Color.decode(c);
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public Color getColor() {
        return this.color;
    }

    public String toCode() {
        return this.color.name().substring(0, 1) + value;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("\u001b[47m");
        switch (this.color) {
            case HEARTS:
                str.append("\u001b[31m");
                break;
            case DIAMONDS:
                str.append("\u001b[31m");
                break;
            case SPADES:
                str.append("\u001b[30m");
                break;
            case CLUBS:
                str.append("\u001b[30m");
                break;
        }
        str.append(this.color.name());
        str.append("\u001b[30m");
        str.append(" ");
        str.append(this.value);
        str.append("\u001b[0m");
        return str.toString();
    }

    enum Color {
        HEARTS,
        DIAMONDS,
        SPADES,
        CLUBS;

        static Color decode(char c) {
            return switch (c) {
                case 'H' -> Color.HEARTS;
                case 'D' -> Color.DIAMONDS;
                case 'S' -> Color.SPADES;
                case 'C' -> Color.CLUBS;
                default -> throw new IllegalArgumentException();
            };
        }
    }
}
