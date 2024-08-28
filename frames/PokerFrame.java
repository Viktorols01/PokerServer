package frames;

import java.awt.Color;

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

    private PokerRenderer renderer;

    public PokerFrame(int width, int height) {
        super(width, height);
        this.renderer = new PokerRenderer();
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
                renderer.renderCard(g, card, x + (fi) * (cardwidth + cardmargin),
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
            renderer.renderString(g, str, x + margin, y + margin, height / 2, new Color(255, 255, 255));
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
        if (player.equals(getModel().getWhoToPlay())) {
            offset = (q) -> {
                return (int) (20 * q);
            };
        } else if (player.equals(getPrevModel().getWhoToPlay())) {
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
                renderer.renderCard(g, card, x + (int) (offset.f(q)) + fi * (cardwidth + cardmargin),
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
            renderer.renderString(g, player.getName(),
                    x + (int) (offset.f(q)) + player.getHand().size() * (cardwidth + cardmargin),
                    y,
                    cardheight / 4, color);
            renderer.renderString(g, "Markers: " + player.getMarkers(),
                    x + (int) (offset.f(q)) + player.getHand().size() * (cardwidth + cardmargin),
                    y + cardheight * 1 / 4,
                    cardheight / 4, color);
            renderer.renderString(g, "Bets: " + player.getBettedMarkers(),
                    x + (int) (offset.f(q)) + player.getHand().size() * (cardwidth + cardmargin),
                    y + cardheight * 2 / 4,
                    cardheight / 5, color);
            renderer.renderString(g,
                    HandRank.rank(CardCollection.join(getModel().getCommunityCards(), player.getHand())).toString(),
                    x + (int) (offset.f(q)) + player.getHand().size() * (cardwidth + cardmargin),
                    y + cardheight * 3 / 4,
                    cardheight / 5, color);

        });
    }
}
