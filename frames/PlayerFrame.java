package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
        jframe.setLayout(new BorderLayout());
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
        this.updateRenderables(getGUI());
    }

    @Override
    protected void onUpdateMessage() {
        getJFrame().setTitle("PlayerFrame | " + getMessage());
    }

    @Override
    protected void updateRenderables(RenderableGUI gui) {
        clearRenderables();
        if (getModel() == null) {
            addRenderable(0, (g, q) -> {
                g.setColor(new Color(255, 0, 0));
                g.fillRect(0, 0, getGUI().getWidth(), getGUI().getHeight());
            });
        } else {
            addRenderable(0, (g, q) -> {
                g.setColor(new Color(0, 100, 0));
                g.fillRect(0, 0, getGUI().getWidth(), getGUI().getHeight());
            });

            {

                final int cardwidth = 80;
                final int cardheight = 130;
                final int cardmargin = 10;
                for (int i = 0; i < getModel().getCommunityCards().size(); i++) {
                    final int fi = i;
                    Card card = getModel().getCommunityCards().get(i);
                    addRenderable(0, (g, q) -> {
                        renderCard(g, card, getGUI().getWidth() / 2 + (fi - 2) * (cardwidth + cardmargin),
                                getGUI().getHeight() / 2, cardwidth, cardheight, cardmargin);
                    });
                }
            }

            {
                final int cardwidth = 50;
                final int cardheight = 80;
                final int cardmargin = 7;
                for (int j = 0; j < getModel().getPlayers().size(); j++) {
                    PlayerData player = getModel().getPlayers().get(j);
                    addPlayerFrame(player, cardmargin, cardmargin + j * (cardheight + cardmargin), cardwidth,
                            cardheight,
                            cardmargin);
                }
            }

            {
                final int cardwidth = 50;
                final int cardheight = 80;
                final int cardmargin = 7;
                for (int i = 0; i < getModel().getYou().getHand().size(); i++) {
                    final int fi = i;
                    Card card = getModel().getYou().getHand().get(i);
                    addRenderable(0, (g, q) -> {
                        renderCard(g, card, fi * (cardwidth + cardmargin), getGUI().getHeight() - cardheight,
                                cardwidth,
                                cardheight,
                                cardmargin);
                    });
                }

                addRenderable(0, (g, q) -> {
                    renderString(g, "Pot: " + getModel().getPot(), cardmargin,
                            getGUI().getHeight() - cardwidth - cardmargin - 90 - 20,
                            20, new Color(0, 0, 0));
                    renderString(g,
                            "Must bet: " + (getModel().getMinBet() - getModel().getYou().getBettedMarkers()),
                            cardmargin,
                            getGUI().getHeight() - cardwidth - cardmargin - 60 - 20, 20, new Color(0, 0, 0));
                    HandRank rank = HandRank
                            .rank(CardCollection.join(getModel().getYou().getHand(),
                                    getModel().getCommunityCards()));
                    renderString(g, "You have: " + rank,
                            cardmargin,
                            getGUI().getHeight() - cardwidth - cardmargin - 30 - 20, 20, new Color(0, 0, 0));
                });
            }
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

    private interface Function {
        public abstract int f(float q);
    }

    protected void addCard(Card card, int x, int y, int cardwidth, int cardheight, int cardmargin) {
        final Function offset;
        offset = (q) -> {
            return 0;
        };
        this.addRenderable(0.1, (g, q) -> {
            renderCard(g, card, x, y + offset.f(q),
                    cardwidth,
                    cardheight,
                    cardmargin);
        });
    }

    protected void addPlayerFrame(PlayerData player, int x, int y, int cardwidth, int cardheight, int cardmargin) {
        final Function offset;
        if (player.getName().equals(getModel().getToPlay().getName())) {
            offset = (q) -> {
                return (int) (20 * q);
            };
        } else if (player.getName().equals(getPrevModel().getToPlay().getName())) {
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
            g.fillRoundRect(x + offset.f(q), y, cardwidth * 7,
                    cardheight, cardmargin, cardmargin);
        });

        for (int i = 0; i < player.getHand().size(); i++) {
            final int fi = i;
            Card card = player.getHand().get(i);
            addRenderable(0.1, (g, q) -> {
                renderCard(g, card, x + offset.f(q) + fi * (cardwidth + cardmargin),
                        y,
                        cardwidth,
                        cardheight,
                        cardmargin);
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
                    x + offset.f(q) + player.getHand().size() * (cardwidth + cardmargin),
                    y,
                    cardheight / 3, color);
            renderString(g, "Markers: " + player.getMarkers(),
                    x + offset.f(q) + player.getHand().size() * (cardwidth + cardmargin),
                    y + cardheight * 1 / 3,
                    cardheight / 3, color);
            renderString(g, "Bets: " + player.getBettedMarkers(),
                    x + offset.f(q) + player.getHand().size() * (cardwidth + cardmargin),
                    y + cardheight * 2 / 3,
                    cardheight / 3, color);
        });
    }
}
