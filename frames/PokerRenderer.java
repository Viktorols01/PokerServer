package frames;

import poker.Card;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PokerRenderer {
    private static BufferedImage hearts;
    private static BufferedImage diamonds;
    private static BufferedImage spades;
    private static BufferedImage clubs;
    private static BufferedImage unknown;

    public PokerRenderer() {
        try {
            hearts = ImageIO.read(new File("images/hearts.png"));
            diamonds = ImageIO.read(new File("images/diamonds.png"));
            spades = ImageIO.read(new File("images/spades.png"));
            clubs = ImageIO.read(new File("images/clubs.png"));
            unknown = ImageIO.read(new File("images/unknown.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void renderCard(Graphics g, Card card, int x, int y, int width, int height, int margin,
            float alpha) {
        g.setColor(new Color(255, 255, 255, (int) (255 * alpha)));
        g.fillRoundRect(x, y, width, height, margin, margin);

        BufferedImage image;
        if (card == null) {
            image = unknown;
            renderImage(g, image, x + margin, y + margin, width - margin * 2, height - margin * 2, alpha);
        } else {
            Card.Suit color = card.getColor();
            switch (color) {
                case DIAMONDS:
                    image = diamonds;
                    break;
                case HEARTS:
                    image = hearts;
                    break;
                case SPADES:
                    image = spades;
                    break;
                case CLUBS:
                    image = clubs;
                    break;
                default:
                    image = null;
                    break;
            }
            renderImage(g, image, x + margin, y + margin, width - margin * 2, height - margin * 2, alpha);

            renderString(g, card.getValueString(), x, y, height / 4, new Color(0, 0, 0, alpha));
        }
    }

    public void renderImage(Graphics g, BufferedImage image, int x, int y, int width, int height,
            float alpha) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.drawImage(image, x, y, width, height, null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
    }

    public void renderString(Graphics g, String str, int x, int y, int size, Color color) {
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, size));
        g.drawString(str, x, y + size);
    }
}
