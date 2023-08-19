package frames;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import poker.Card;
import poker.HoldEmModel;
import tools.RenderableFrame;

public abstract class PokerFrame extends RenderableFrame {

    private HoldEmModel prevModel;
    private HoldEmModel model;
    private String message;

    private static BufferedImage hearts;
    private static BufferedImage diamonds;
    private static BufferedImage spades;
    private static BufferedImage clubs;
    private static BufferedImage unknown;

    public PokerFrame(int width, int height) {
        super(width, height);
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

    protected abstract void onUpdateModel();

    protected abstract void onUpdateMessage();

    public void updateModel(HoldEmModel newModel) {
        if (this.model == null) {
            this.prevModel = newModel;
        } else {
            this.prevModel = this.model;
        }
        this.model = newModel;
        onUpdateModel();
    }

    public void updateMessage(String message) {
        this.message = message;
        onUpdateMessage();
    }

    protected HoldEmModel getModel() {
        return model;
    }

    protected HoldEmModel getPrevModel() {
        return prevModel;
    }

    protected String getMessage() {
        return message;
    }

    protected static void renderCard(Graphics g, Card card, int x, int y, int width, int height, int margin) {
        g.setColor(new Color(255, 255, 255));
        g.fillRoundRect(x, y, width, height, margin, margin);

        BufferedImage image;
        if (card == null) {
            image = unknown;
            g.drawImage(image, x + margin, y + margin, width - margin * 2, height - margin * 2, null);
        } else {
            Card.Color color = card.getColor();
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
            g.drawImage(image, x + margin, y + margin, width - margin * 2, height - margin * 2, null);
            renderString(g, String.valueOf(card.getValue()), x, y, height / 4, new Color(0, 0, 0));
        }
    }

    protected static void renderString(Graphics g, String str, int x, int y, int size, Color color) {
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, size));
        g.drawString(str, x, y + size);
    }
}
