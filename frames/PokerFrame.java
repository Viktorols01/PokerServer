package frames;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import poker.Card;
import poker.CardCollection;
import poker.HandRank;
import poker.HoldEmModel;
import poker.PlayerData;
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

    protected interface Function {
        public abstract float f(float q);
    }

    protected final void addGeneralBoardview() {
        {
            addRenderable(0, (g, q) -> {
                g.setColor(new Color(0, 100, 0));
                g.fillRect(0, 0, getGUI().getWidth(), getGUI().getHeight());
            });
            {

                final int cardwidth = 80;
                final int cardheight = 130;
                final int cardmargin = 10;
                final int x = getGUI().getWidth() / 2 - 2 * cardwidth;
                final int y = getGUI().getHeight() / 2;
                addCommunityCards(x, y, cardwidth, cardheight, cardmargin);
            }

            {
                final int cardwidth = 50;
                final int cardheight = 80;
                final int cardmargin = 7;
                addPlayerFrames(cardmargin, cardmargin, cardwidth, cardheight, cardmargin);
            }

            {
                final int height = 100;
                final int width = 200;
                final int margin = 20;
                addPot(getGUI().getWidth() / 2, margin, width, height, margin);
                addMessage(margin, getGUI().getHeight() - height - margin, width * 4, height, margin);
            }
        }
    }

    protected final void addCommunityCards(int x, int y, int cardwidth, int cardheight, int cardmargin) {
        for (int i = 0; i < 5; i++) {
            final int fi = i;
            final Function alpha;
            final Function offsetY;
            final Card card;
            if (getModel().getCommunityCards().hasIndex(i)) {
                card = getModel().getCommunityCards().get(i);
            } else if (getPrevModel().getCommunityCards().hasIndex(i)) {
                card = getPrevModel().getCommunityCards().get(i);
            } else {
                break;
            }

            if (i >= getPrevModel().getCommunityCards().size()) {
                offsetY = (q) -> {
                    return -50 + 50 * q;
                };
                alpha = (q) -> {
                    return q;
                };
            } else if (i < getModel().getCommunityCards().size()) {
                offsetY = (q) -> {
                    return 0;
                };
                alpha = (q) -> {
                    return 1;
                };
            } else {
                offsetY = (q) -> {
                    return 50 * q;
                };
                alpha = (q) -> {
                    return 1 - q;
                };
            }

            addRenderable(0.2, (g, q) -> {
                renderCard(g, card, x + (fi) * (cardwidth + cardmargin),
                        y + (int) (offsetY.f(q)), cardwidth, cardheight, cardmargin, alpha.f(q));
            });
        }
    }

    protected final void addYouFrame(int x, int y, int width, int height, int margin) {
        addStringBox(getModel().getYou().getName(), x, y, width, height / 2, margin / 2);
        addStringBox(
                "You have: " + HandRank
                        .rank(CardCollection.join(getModel().getYou().getHand(), getModel().getCommunityCards())),
                x, y + height / 2, width, height / 2, margin / 2);
    }

    protected final void addPot(int x, int y, int width, int height, int margin) {
        addStringBox("" + getModel().getPot(), x, y, width, height, margin);
    }

    protected final void addRemainingBets(int x, int y, int width, int height, int margin) {
        addStringBox("Markers to join: " + (getModel().getRemainingBet()) + ".", x,
                y, width, height, margin);
    }

    protected final void addMessage(int x, int y, int width, int height, int margin) {
        addStringBox(getMessage(), x, y, width, height, margin);
    }

    protected void addStringBox(String str, int x, int y, int width, int height, int margin) {
        this.addRenderable(0, (g, q) -> {
            g.setColor(new Color(0, 0, 0, 100));
            g.fillRoundRect(x, y, width,
                    height, 5, 5);
            renderString(g, str, x + margin, y + margin, height / 2, new Color(255, 255, 255));
        });
    }

    protected final void addPlayerFrames(int x, int y, int cardwidth, int cardheight, int cardmargin) {
        for (int j = 0; j < getModel().getPlayers().size(); j++) {
            PlayerData player = getModel().getPlayers().get(j);
            addPlayerFrame(player, x, y + j * (cardheight + cardmargin), cardwidth, cardheight, cardmargin);
        }
    }

    protected final void addPlayerFrame(PlayerData player, int x, int y, int cardwidth, int cardheight,
            int cardmargin) {
        final Function offset;
        if (player.equals(getModel().getToPlay())) {
            offset = (q) -> {
                return (int) (20 * q);
            };
        } else if (player.equals(getPrevModel().getToPlay())) {
            offset = (q) -> {
                return 20 - (int) (20 * q);
            };
        } else {
            offset = (q) -> {
                return 0;
            };
        }

        this.addRenderable(0.1, (g, q) -> {
            g.setColor(new Color(0, 0, 0, 100));
            g.fillRoundRect(x + (int) offset.f(q), y, cardwidth * 7,
                    cardheight, cardmargin, cardmargin);
        });

        for (int i = 0; i < player.getHand().size(); i++) {
            final int fi = i;
            Card card = player.getHand().get(i);
            addRenderable(0.1, (g, q) -> {
                renderCard(g, card, x + (int) (offset.f(q)) + fi * (cardwidth + cardmargin),
                        y,
                        cardwidth,
                        cardheight,
                        cardmargin,
                        1);
            });
        }
        final Color color;
        if (player.hasFolded()) {
            color = new Color(100, 100, 100);
        } else {
            color = new Color(255, 255, 255);
        }
        addRenderable(0.1, (g, q) -> {
            renderString(g, player.getName(),
                    x + (int) (offset.f(q)) + player.getHand().size() * (cardwidth + cardmargin),
                    y,
                    cardheight / 4, color);
            renderString(g, "Markers: " + player.getMarkers(),
                    x + (int) (offset.f(q)) + player.getHand().size() * (cardwidth + cardmargin),
                    y + cardheight * 1 / 4,
                    cardheight / 4, color);
            renderString(g, "Bets: " + player.getBettedMarkers(),
                    x + (int) (offset.f(q)) + player.getHand().size() * (cardwidth + cardmargin),
                    y + cardheight * 2 / 4,
                    cardheight / 5, color);
            renderString(g,
                    HandRank.rank(CardCollection.join(getModel().getCommunityCards(), player.getHand())).toString(),
                    x + (int) (offset.f(q)) + player.getHand().size() * (cardwidth + cardmargin),
                    y + cardheight * 3 / 4,
                    cardheight / 5, color);

        });
    }

    protected static void renderCard(Graphics g, Card card, int x, int y, int width, int height, int margin,
            float alpha) {
        g.setColor(new Color(255, 255, 255, (int) (255 * alpha)));
        g.fillRoundRect(x, y, width, height, margin, margin);

        BufferedImage image;
        if (card == null) {
            image = unknown;
            renderImage(g, image, x + margin, y + margin, width - margin * 2, height - margin * 2, alpha);
        } else {
            Card.Color color = card.getColor();
            int value = card.getValue();
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
            String str;
            switch (value) {
                case 1:
                    str = "E";
                    break;
                case 11:
                    str = "J";
                    break;
                case 12:
                    str = "Q";
                    break;
                case 13:
                    str = "K";
                    break;
                default:
                    str = String.valueOf(card.getValue());
                    break;
            }
            renderImage(g, image, x + margin, y + margin, width - margin * 2, height - margin * 2, alpha);

            renderString(g, str, x, y, height / 4, new Color(0, 0, 0, alpha));
        }
    }

    protected static void renderImage(Graphics g, BufferedImage image, int x, int y, int width, int height,
            float alpha) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.drawImage(image, x, y, width, height, null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
    }

    protected static void renderString(Graphics g, String str, int x, int y, int size, Color color) {
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, size));
        g.drawString(str, x, y + size);
    }
}
