package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import poker.Card;
import poker.HoldEmModel;
import tools.GUI;

public abstract class PokerFrame {

    private JFrame jframe;

    private PokerGUI gui;

    private HoldEmModel model;
    private String message;

    private static BufferedImage hearts;
    private static BufferedImage diamonds;
    private static BufferedImage spades;
    private static BufferedImage clubs;
    private static BufferedImage unknown;

    public PokerFrame(int width, int height) {
        this.jframe = new JFrame();
        this.gui = new PokerGUI(width, height);
        try {
            hearts = ImageIO.read(new File("images/hearts.png"));
            diamonds = ImageIO.read(new File("images/diamonds.png"));
            spades = ImageIO.read(new File("images/spades.png"));
            clubs = ImageIO.read(new File("images/clubs.png"));
            unknown = ImageIO.read(new File("images/unknown.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        initJFrame();
    }

    protected abstract void addComponents(JFrame jframe);

    protected abstract void renderGUI(Graphics g);

    protected abstract void onUpdateModel();

    protected abstract void onUpdateMessage();

    private void initJFrame() {
        jframe.setDefaultCloseOperation(3);
        jframe.setLayout(new BorderLayout());
        addComponents(jframe);
        jframe.pack();
        jframe.setResizable(false);
        jframe.setVisible(true);
    }

    public void updateModel(HoldEmModel model) {
        this.model = model;
        onUpdateModel();
    }

    public void updateMessage(String message) {
        this.message = message;
        onUpdateMessage();
    }

    protected class PokerGUI extends GUI {

        public PokerGUI(int width, int height) {
            super(width, height, 60);
        }

        @Override
        protected void setup() {
        }

        @Override
        protected void update() {
        }

        @Override
        protected void render(Graphics g) {
            renderGUI(g);
        }
    }

    public JFrame getJFrame() {
        return jframe;
    }

    public PokerGUI getGUI() {
        return gui;
    }

    public HoldEmModel getModel() {
        return model;
    }

    public String getMessage() {
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
