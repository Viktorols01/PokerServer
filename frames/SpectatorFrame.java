package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

public class SpectatorFrame extends PokerFrame {

    private JButton continueButton;

    private boolean canContinue;

    public SpectatorFrame(int width, int height) {
        super(width, height);
        getJFrame().setTitle("SpectatorFrame");
    }

    @Override
    protected void addComponents(JFrame jframe) {
        jframe.setLayout(new BorderLayout());
        Container container = new Container();
        container.setLayout(new FlowLayout());
        container.setPreferredSize(new Dimension(getJFrame().getWidth(), 40));
        this.continueButton = new JButton("continue");
        this.continueButton.addActionListener((e) -> {
            this.canContinue = true;
        });
        container.add(continueButton);
        jframe.add(container, BorderLayout.SOUTH);
        jframe.add(getGUI(), BorderLayout.CENTER);
    }

    @Override
    protected void onUpdateModel() {
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

    public String[] getContinue() {
        updateRenderables();
        while (!this.canContinue) {
            Thread.yield();
        }
        this.canContinue = false;
        return new String[] {};
    }
}
