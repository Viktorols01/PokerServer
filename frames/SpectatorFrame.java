package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;

import poker.Card;
import poker.CardCollection;
import poker.HandRank;
import poker.PlayerData;

public class SpectatorFrame extends PokerFrame {

    public SpectatorFrame(int width, int height) {
        super(width, height);
    }

    @Override
    protected void addComponents(JFrame jframe) {
        jframe.add(getGUI(), BorderLayout.CENTER);
    }

    @Override
    protected void onUpdateModel() {
    }

    @Override
    protected void onUpdateMessage() {
        getJFrame().setTitle("SpectatorFrame | " + getMessage());
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
                HandRank rank = HandRank
                        .rank(CardCollection.join(player.getHand(), getModel().getCommunityCards()));
                renderString(g, player.getName() + " has " + rank,
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
        }
    }
}