package frames;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import bots.MyBot;
import client.BotClient;
import client.PokerClient;
import client.SpectatorClient;
import client.YouClient;
import server.PokerServer;

public class MenuFrame extends JFrame {

    JTextField portField;
    JTextField nameField;

    public MenuFrame() {
        JTabbedPane tabbedPane = new JTabbedPane();
        //tabbedPane.setPreferredSize(new Dimension(600, 400));

        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, 1));
        JLabel titleLabel = new JLabel("PokerServer");
        titleLabel.setFont(new Font("Serif", Font.PLAIN, 36));
        homePanel.add(titleLabel);
        JLabel credLabel = new JLabel("By Viktor Ols");
        credLabel.setFont(new Font("Serif", Font.PLAIN, 12));
        homePanel.add(credLabel);
        homePanel.add(new JLabel(" "));
        homePanel.add(new JLabel("Currentry using port:"));
        portField = new JTextField("50160");
        portField.setPreferredSize(new Dimension(200,50));
        homePanel.add(portField);
        tabbedPane.add(homePanel, "Home");

        JPanel clientPanel = new JPanel();
        nameField = new JTextField("Name");
        clientPanel.add(nameField);
        JButton botButton = new JButton("Let a bot play");
        botButton.addActionListener((e) -> {
            PokerClient client = new BotClient(getClientName(), new MyBot(),
                            true);
            client.connect("localhost", getPort());
        });
        clientPanel.add(botButton);
        JButton youButton = new JButton("Let you play");
        youButton.addActionListener((e) -> {
            PokerClient client = new YouClient(getClientName(), false);
            client.connect("localhost", getPort());
        });
        clientPanel.add(youButton);
        JButton spectateButton = new JButton("Let you watch");
        spectateButton.addActionListener((e) -> {
            PokerClient client = new SpectatorClient(getClientName(), false);
            client.connect("localhost", getPort());
        });
        clientPanel.add(spectateButton);
        tabbedPane.add(clientPanel, "Create client");

        JPanel serverPanel = new JPanel();
        JButton serverButton = new JButton("Create server");
        serverButton.addActionListener((e) -> {
            new ServerFrame(new PokerServer(getPort()), 1200, 800);
        });
        serverPanel.add(serverButton);
        tabbedPane.add(serverPanel, "Create server");

        this.add(tabbedPane);
        this.pack();
        this.setDefaultCloseOperation(3);
        this.setVisible(true);

    }

    private String getClientName() {
        return this.nameField.getText();
    }

    private int getPort() {
        return Integer.valueOf(this.portField.getText());
    }
}
