package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToggleButton;

import comms.Connection;
import server.PokerServer;

public class ServerFrame extends PokerFrame {

    private PokerServer server;

    private JButton openButton;
    private JButton closeButton;
    private JButton startButton;
    private JToggleButton continueGameButton;
    private boolean continueGame;

    private Thread gameListener;
    private Thread joinListener;

    public ServerFrame(PokerServer server, int width, int height) {
        super(width, height);
        this.server = server;
        getJFrame().setTitle("ServerFrame");
        addListeners();

        updateRenderables();
    }

    private void addListeners() {
        gameListener = new Thread(() -> {
            while (true) {
                try {
                    String message = server.getGame().getBroadcaster().receive();
                    updateModel(server.getGame().getHoldEmModel());
                    updateMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        gameListener.start();
        joinListener = new Thread(() -> {
            while (true) {
                try {
                    server.getBroadcaster().receive("player joined");
                    updateRenderables();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        joinListener.start();
    }

    @Override
    protected void addComponents(JFrame jframe) {
        jframe.setLayout(new BorderLayout());
        Container container = new Container();
        container.setLayout(new FlowLayout());
        container.setPreferredSize(new Dimension(getJFrame().getWidth(), 40));
        openButton = new JButton("open");
        openButton.addActionListener((e) -> {
            server.openConnections();
        });
        container.add(openButton);
        closeButton = new JButton("close");
        closeButton.addActionListener((e) -> {
            server.closeConnections();
        });
        container.add(closeButton);
        startButton = new JButton("start");
        startButton.addActionListener((e) -> {
            server.startGame();
        });
        container.add(startButton);
        continueGameButton = new JToggleButton("pause");
        continueGameButton.addActionListener((e) -> {
            if (continueGame) {
                continueGame = false;
                continueGameButton.setText("resume");
            } else {
                continueGame = true;
                continueGameButton.setText("pause");
            }
        });
        container.add(continueGameButton);
        jframe.add(getGUI(), BorderLayout.CENTER);
        jframe.add(container, BorderLayout.SOUTH);
    }

    @Override
    public void updateRenderables() {
        getGUI().clearRenderables();
        if (getModel() != null) {
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
        } else {
            final int height = 50;
            final int width = 500;
            final int margin = 10;
            addStringBox("Server has not started yet!", margin, margin, width * 2, height * 2, margin * 2);
            for (int i = 0; i < server.getConnections().size(); i++) {
                Connection connection = server.getConnections().get(i);
                addStringBox(connection.getName() + " (" + connection.getIP() + ")", margin,
                        (height + margin) * 2 + margin + i * (height + margin), width, height,
                        margin);
            }
        }
    }

    @Override
    protected void onUpdateModel() {
        updateRenderables();
    }

    @Override
    protected void onUpdateMessage() {
        updateRenderables();
    }

}