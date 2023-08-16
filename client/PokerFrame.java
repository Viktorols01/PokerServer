package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import poker.Card;
import poker.PlayerData;
import poker.PokerModel;
import tools.GUI;

public class PokerFrame {

    private JFrame jframe;

    private PokerGUI gui;
    private JButton checkButton;
    private JButton raiseButton;
    private JTextField raiseTextField;
    private JButton foldButton;

    private boolean hasMove;
    private String[] move;

    public PokerFrame() {
        this.jframe = new JFrame();
        this.gui = new PokerGUI();
        initJFrame();
    }

    private void initJFrame() {
        jframe.setDefaultCloseOperation(3);
        jframe.setLayout(new BorderLayout());
        Container container = new Container();
        container.setLayout(new FlowLayout());
        container.setPreferredSize(new Dimension(jframe.getWidth(), 40));
        this.checkButton = new JButton("check");
        this.checkButton.addActionListener((e) -> {
            this.move = new String[] { "check", "0" };
            this.hasMove = true;
        });
        container.add(checkButton);
        this.raiseButton = new JButton("raise");
        this.raiseButton.addActionListener((e) -> {
            this.move = new String[] { "raise", this.raiseTextField.getText() };
            this.hasMove = true;
        });
        container.add(raiseButton);
        this.raiseTextField = new JTextField("100");
        this.raiseTextField.setPreferredSize(new Dimension(100, 30));
        container.add(raiseTextField);
        this.foldButton = new JButton("fold");
        this.foldButton.addActionListener((e) -> {
            this.move = new String[] { "fold", "0" };
            this.hasMove = true;
        });
        container.add(foldButton);
        jframe.add(container, BorderLayout.SOUTH);
        jframe.add(gui, BorderLayout.CENTER);
        jframe.pack();
        jframe.setResizable(false);
        jframe.setVisible(true);
    }

    public void updateModel(PokerModel model) {
        gui.updateModel(model);
    }

    public void updateMessage(String message) {
        jframe.setTitle("PokerClient | Message from server: " + message);
    }

    private boolean hasMove() {
        return this.hasMove;
    }

    public String[] getMove() {
        while (!hasMove()) {
            Thread.yield();
        }
        this.hasMove = false;
        return this.move;
    }

    private class PokerGUI extends GUI {
        private PokerModel model;

        private static BufferedImage hearts;
        private static BufferedImage diamonds;
        private static BufferedImage spades;
        private static BufferedImage clubs;
        private static BufferedImage unknown;

        public PokerGUI() {
            super(1000, 600, 60);
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

        public void updateModel(PokerModel model) {
            this.model = model;
        }

        @Override
        protected void setup() {
        }

        @Override
        protected void update() {
        }

        @Override
        protected void render(Graphics g) {
            if (model == null) {
                g.setColor(new Color(255, 0, 0));
                g.fillRect(0, 0, getWidth(), getHeight());
            } else {
                renderModel(g, model);
            }
        }

        private void renderModel(Graphics g, PokerModel model) {
            g.setColor(new Color(0, 100, 0));
            g.fillRect(0, 0, getWidth(), getHeight());

            int cardwidth;
            int cardheight;
            int cardmargin;

            cardwidth = 80;
            cardheight = 130;
            cardmargin = 10;
            for (int i = 0; i < model.getCommunityCards().size(); i++) {
                Card card = model.getCommunityCards().get(i);
                renderCard(g, card, getWidth() / 2 + (i - 2) * (cardwidth + cardmargin), getHeight() / 2, cardwidth,
                        cardheight,
                        cardmargin);
            }

            cardwidth = 25;
            cardheight = 40;
            cardmargin = 5;
            for (int j = 0; j < model.getPlayers().size(); j++) {
                PlayerData player = model.getPlayers().get(j);
                for (int i = 0; i < player.getHand().size(); i++) {
                    Card card = player.getHand().get(i);
                    renderCard(g, card, i * (cardwidth + cardmargin), j * (cardheight + cardmargin), cardwidth,
                            cardheight,
                            cardmargin);
                }
                if (player.hasFolded()) {
                    g.setColor(new Color(100, 100, 100));
                } else {
                    g.setColor(new Color(255, 255, 255));
                }
                g.setFont(new Font("Arial", Font.BOLD, cardheight - 10));
                g.drawString(player.getName() + " (" + player.getMarkers() + ") (" + player.getBettedMarkers() + ")",
                        player.getHand().size() * (cardwidth + cardmargin), j * (cardheight + cardmargin) + cardheight);
            }

            cardwidth = 50;
            cardheight = 80;
            cardmargin = 10;
            for (int i = 0; i < model.getYou().getHand().size(); i++) {
                Card card = model.getYou().getHand().get(i);
                g.setColor(new Color(0, 0, 0));
                renderCard(g, card, i * (cardwidth + cardmargin), getHeight() - cardheight, cardwidth, cardheight,
                        cardmargin);
            }
            g.setColor(new Color(0, 0, 0));
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Pot: " + model.getPot(), cardmargin, getHeight() - cardwidth - cardmargin - 60);
            g.drawString("Must bet: " + (model.getMinBet() - model.getYou().getBettedMarkers()), cardmargin,
                    getHeight() - cardwidth - cardmargin - 30);
        }

        private static void renderCard(Graphics g, Card card, int x, int y, int width, int height, int margin) {
            g.setColor(new Color(255, 255, 255));
            g.fillRect(x, y, width, height);

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
                g.setColor(new Color(0, 0, 0));
                g.setFont(new Font("Arial", Font.BOLD, height / 4));
                g.drawString(String.valueOf(card.getValue()), x, y + height / 4);
            }
        }
    }
}
