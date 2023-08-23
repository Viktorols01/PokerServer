package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;

import comms.Broadcaster;
import comms.Connection;
import server.PokerPlayer;
import server.PokerServer;

public class ServerFrame extends PokerFrame {

    private PokerServer server;

    private List<PokerPlayer> players;

    private JButton openButton;
    private JButton closeButton;
    private JButton startButton;

    private Thread gameListener;
    private Thread joinListener;

    public ServerFrame(PokerServer server, int width, int height) {
        super(width, height);
        this.server = server;
        getJFrame().setTitle("ServerFrame | closed");
        addListeners();

        updateRenderables();
    }

    private void addListeners() {
        gameListener = new Thread(() -> {
            while (true) {
                try {
                    Broadcaster updateSender = server.getGame().getUpdateSender();
                    updateSender.receive();
                    this.players = server.getGame().getPlayers();
                    updateRenderables();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        gameListener.start();
        joinListener = new Thread(() -> {
            while (true) {
                try {
                    Broadcaster joinedSender = server.getJoinedSender();
                    joinedSender.receive();
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
            updateRenderables();
            getJFrame().setTitle("ServerFrame | open");
        });
        container.add(openButton);
        closeButton = new JButton("close");
        closeButton.addActionListener((e) -> {
            server.closeConnections();
            updateRenderables();
            getJFrame().setTitle("ServerFrame | closed");
        });
        container.add(closeButton);
        startButton = new JButton("restart");
        startButton.addActionListener((e) -> {
            server.startGame();
            updateRenderables();
        });
        container.add(startButton);
        jframe.add(getGUI(), BorderLayout.CENTER);
        jframe.add(container, BorderLayout.SOUTH);
    }

    @Override
    public void updateRenderables() {
        getGUI().clearRenderables();
        if (this.players != null) {
            addRenderable(0, (g, q) -> {
                g.setColor(new Color(0, 100, 0));
                g.fillRect(0, 0, getGUI().getWidth(), getGUI().getHeight());
            });
            final int height = 30;
            final int width = 160;
            final int margin = 10;
            for (int i = 0; i < players.size(); i++) {
                PokerPlayer player = players.get(i);
                addStringBox(player.getName(), margin, margin + i * (height + margin), width, height, margin);
                addStringBox("Markers: " + player.getPlayerData().getMarkers(), margin + 1 * (width + margin),
                        margin + i * (height + margin), width, height, margin);
                addStringBox("Checks: " + player.getPlayerStatistics().getChecks(), margin + 2 * (width + margin),
                        margin + i * (height + margin), width, height, margin);
                addStringBox("Matches: " + player.getPlayerStatistics().getMatches(), margin + 3 * (width + margin),
                        margin + i * (height + margin), width, height, margin);
                addStringBox("Raises: " + player.getPlayerStatistics().getRaises(), margin + 4 * (width + margin),
                        margin + i * (height + margin), width, height, margin);
                addStringBox("Average raises: " + player.getPlayerStatistics().getAverageRaise(),
                        margin + 5 * (width + margin),
                        margin + i * (height + margin), width, height, margin);
                addStringBox("Folds: " + player.getPlayerStatistics().getFolds(), margin + 6 * (width + margin),
                        margin + i * (height + margin), width, height, margin);
            }
        } else {
            final int height = 50;
            final int width = 500;
            final int margin = 10;
            addStringBox("Server is " + (server.isOpen() ? "open" : "closed") + ".", margin, margin, width * 2,
                    height * 2, margin * 2);
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