package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import poker.Card;
import poker.CardCollection;
import poker.HandRank;
import poker.PlayerData;

public class PlayerFrame extends PokerFrame {

    private JButton checkButton;
    private JButton raiseButton;
    private JTextField raiseTextField;
    private JButton foldButton;

    private boolean hasMove;
    private String[] move;

    public PlayerFrame(int width, int height) {
        super(width, height);
    }

    @Override
    protected void addComponents(JFrame jframe) {
        Container container = new Container();
        container.setLayout(new FlowLayout());
        container.setPreferredSize(new Dimension(getJFrame().getWidth(), 40));
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
        jframe.add(getGUI(), BorderLayout.CENTER);
    }

    @Override
    protected void onUpdateModel() {
        this.checkButton.setText("check (" + (getModel().getMinBet() - getModel().getYou().getBettedMarkers()) + ")");
    }

    @Override
    protected void onUpdateMessage() {
        getJFrame().setTitle("PlayerFrame | " + getMessage());
    }

    @Override
    protected void renderGUI(Graphics g) {
        if (getModel() == null) {
            g.setColor(new Color(255, 0, 0));
            g.fillRect(0, 0, getGUI().getWidth(), getGUI().getHeight());
        } else {
            g.setColor(new Color(0, 100, 0));
            g.fillRect(0, 0, getGUI().getWidth(), getGUI().getHeight());

            int cardwidth;
            int cardheight;
            int cardmargin;

            cardwidth = 80;
            cardheight = 130;
            cardmargin = 10;
            for (int i = 0; i < getModel().getCommunityCards().size(); i++) {
                Card card = getModel().getCommunityCards().get(i);
                renderCard(g, card, getGUI().getWidth() / 2 + (i - 2) * (cardwidth + cardmargin),
                        getGUI().getHeight() / 2, cardwidth, cardheight, cardmargin);
            }

            cardwidth = 50;
            cardheight = 80;
            cardmargin = 7;
            for (int j = 0; j < getModel().getPlayers().size(); j++) {
                PlayerData player = getModel().getPlayers().get(j);
                int offset;
                if (player == getModel().getToPlay()) {
                    offset = 20;
                } else {
                    offset = 0;
                }
                for (int i = 0; i < player.getHand().size(); i++) {
                    Card card = player.getHand().get(i);
                    renderCard(g, card, offset + i * (cardwidth + cardmargin), j * (cardheight + cardmargin), cardwidth,
                            cardheight,
                            cardmargin);
                }
                Color color;
                if (player.hasFolded()) {
                    color = new Color(100, 100, 100);
                } else {
                    color = new Color(255, 255, 255);
                }
                renderString(g, player.getName(),
                        offset + player.getHand().size() * (cardwidth + cardmargin), j * (cardheight + cardmargin),
                        cardheight / 3, color);
                renderString(g, "Markers: " + player.getMarkers(),
                        offset + player.getHand().size() * (cardwidth + cardmargin),
                        j * (cardheight + cardmargin) + cardheight * 1 / 3,
                        cardheight / 3, color);
                renderString(g, "Bets: " + player.getBettedMarkers(),
                        offset + player.getHand().size() * (cardwidth + cardmargin),
                        j * (cardheight + cardmargin) + cardheight * 2 / 3,
                        cardheight / 3, color);
            }

            cardwidth = 50;
            cardheight = 80;
            cardmargin = 7;
            for (int i = 0; i < getModel().getYou().getHand().size(); i++) {
                Card card = getModel().getYou().getHand().get(i);
                g.setColor(new Color(0, 0, 0));
                renderCard(g, card, i * (cardwidth + cardmargin), getGUI().getHeight() - cardheight, cardwidth,
                        cardheight,
                        cardmargin);
            }
            renderString(g, "Pot: " + getModel().getPot(), cardmargin,
                    getGUI().getHeight() - cardwidth - cardmargin - 90 - 20,
                    20, new Color(0, 0, 0));
            renderString(g, "Must bet: " + (getModel().getMinBet() - getModel().getYou().getBettedMarkers()),
                    cardmargin,
                    getGUI().getHeight() - cardwidth - cardmargin - 60 - 20, 20, new Color(0, 0, 0));
            HandRank rank = HandRank
                    .rank(CardCollection.join(getModel().getYou().getHand(), getModel().getCommunityCards()));
            renderString(g, "You have: " + rank,
                    cardmargin,
                    getGUI().getHeight() - cardwidth - cardmargin - 30 - 20, 20, new Color(0, 0, 0));
        }
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
}
