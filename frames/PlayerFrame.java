package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class PlayerFrame extends PokerFrame {

    private JButton checkButton;
    private JButton raiseButton;
    private JTextField raiseTextField;
    private JButton foldButton;

    private boolean hasMove;
    private String[] move;

    public PlayerFrame(int width, int height) {
        super(width, height);
        getJFrame().setTitle("PlayerFrame");
    }

    @Override
    protected void addComponents(JFrame jframe) {
        jframe.setLayout(new BorderLayout());
        Container container = new Container();
        container.setLayout(new FlowLayout());
        container.setPreferredSize(new Dimension(getJFrame().getWidth(), 40));
        this.checkButton = new JButton("match");
        this.checkButton.addActionListener((e) -> {
            this.move = new String[] { "match", "0" };
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
        this.checkButton.setText("match (" + (getModel().getMinBet() - getModel().getYou().getBettedMarkers()) + ")");
        this.updateRenderables();
    }

    @Override
    protected void onUpdateMessage() {
        this.updateRenderables();
    }

    @Override
    public void updateRenderables() {
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
                addYouFrame(0, getGUI().getHeight() - height, width * 2, height, margin);
                addPot(getGUI().getWidth() / 2, margin, width, height, margin);
                addMessage(width * 2 + margin, getGUI().getHeight() - height, width * 2, height / 2, margin / 2);
                addRemainingBets(width * 2 + margin, getGUI().getHeight() - height / 2, width * 2, height / 2,
                        margin / 2);
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
}
