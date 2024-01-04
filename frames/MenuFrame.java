package frames;

import java.awt.Dimension;
import java.net.Inet4Address;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import bots.MyBot;
import client.BotClient;
import client.PokerClient;
import client.SpectatorClient;
import client.YouClient;
import server.PokerServer;

public class MenuFrame extends JFrame {

    JTextField nameField;
    JTextField portField;
    JTextField ipField;

    public MenuFrame() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(600, 500));

        {
            JPanel instructionPanel = new JPanel();
            instructionPanel.setLayout(new BoxLayout(instructionPanel, 1));
            JLabel credLabel = new JLabel("Created by Viktor Ols");
            instructionPanel.add(credLabel);
            instructionPanel.add(new JLabel(" "));
            instructionPanel.add(new JLabel("Instructions:"));
            instructionPanel.add(new JLabel("To play with others, make sure you are both connected to the same network."));
            instructionPanel.add(new JLabel("The network needs to be trusted by your firewall."));
            instructionPanel.add(new JLabel("You can join the server running on your computer by connecting to \"localhost\"."));
            instructionPanel.add(new JLabel(" "));
            instructionPanel.add(new JLabel("If you wish to modify your bot, rewrite the contents in file \"bots/MyBot.java\"."));
            instructionPanel.add(new JLabel("For further instructions on bot-making, refer to the file \"BOTMAKING\"."));
            
            instructionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            tabbedPane.add(instructionPanel, "Instructions");
        }

        {
            JPanel clientPanel = new JPanel();
            SpringLayout layout = new SpringLayout();
            clientPanel.setLayout(layout);
            
            JLabel nameLabel = new JLabel("Name: ");
            nameField = new JTextField("Player");
            nameField.setPreferredSize(new Dimension(200, 25));
            clientPanel.add(nameLabel);
            clientPanel.add(nameField);
            layout.putConstraint(SpringLayout.WEST, nameField, 5, SpringLayout.EAST, nameLabel);
            layout.putConstraint(SpringLayout.NORTH, nameField, 5, SpringLayout.NORTH, clientPanel);

            JLabel ipLabel = new JLabel("IP-address: ");
            ipField = new JTextField("localhost");
            ipField.setPreferredSize(new Dimension(100, 25));
            clientPanel.add(ipLabel);
            layout.putConstraint(SpringLayout.NORTH, ipLabel, 5, SpringLayout.SOUTH, nameField);
            clientPanel.add(ipField);
            layout.putConstraint(SpringLayout.WEST, ipField, 5, SpringLayout.EAST, ipLabel);
            layout.putConstraint(SpringLayout.NORTH, ipField, 5, SpringLayout.SOUTH, nameField);

            JButton botButton = new JButton("Bot play");
            botButton.addActionListener((e) -> {
                PokerClient client = new BotClient("BOT " + getClientName(), new MyBot(),
                        true);
                client.connect(getIP(), getPort());
            });
            clientPanel.add(botButton);
            layout.putConstraint(SpringLayout.NORTH, botButton, 5, SpringLayout.SOUTH, ipField);

            JButton youButton = new JButton("You play");
            youButton.addActionListener((e) -> {
                PokerClient client = new YouClient("PLAYER " + getClientName(), false);
                client.connect(getIP(), getPort());
            });
            clientPanel.add(youButton);
            layout.putConstraint(SpringLayout.NORTH, youButton, 5, SpringLayout.SOUTH, botButton);

            JButton spectateButton = new JButton("Spectate");
            spectateButton.addActionListener((e) -> {
                PokerClient client = new SpectatorClient("SPECTATOR " + getClientName(), false);
                client.connect(getIP(), getPort());
            });
            clientPanel.add(spectateButton);
            layout.putConstraint(SpringLayout.NORTH, spectateButton, 5, SpringLayout.SOUTH, youButton);

            clientPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            tabbedPane.add(clientPanel, "Create client");

        }

        {
            JPanel serverPanel = new JPanel();
            serverPanel.setLayout(new BoxLayout(serverPanel, 1));
            try {
                serverPanel.add(new JLabel("IP-address: " + Inet4Address.getLocalHost().getHostAddress()));
            } catch (UnknownHostException e) {
                serverPanel.add(new JLabel("IP-address: " + "ERROR"));
            }
            JButton serverButton = new JButton("Create server");
            serverButton.addActionListener((e) -> {
                new ServerFrame(new PokerServer(getPort()), 1200, 800);
            });
            serverPanel.add(serverButton);

            serverPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            tabbedPane.add(serverPanel, "Create server");
        }

        {
            JPanel settingsPanel = new JPanel();
            SpringLayout layout = new SpringLayout();
            settingsPanel.setLayout(layout);
     
            JLabel portLabel = new JLabel("Port: ");
            portField = new JTextField("50160");
            portField.setPreferredSize(new Dimension(100, 25));
            settingsPanel.add(portLabel);
            layout.putConstraint(SpringLayout.WEST, portLabel, 5, SpringLayout.WEST, settingsPanel);
            layout.putConstraint(SpringLayout.NORTH, portLabel, 5, SpringLayout.NORTH, settingsPanel);
            settingsPanel.add(portField);
            layout.putConstraint(SpringLayout.WEST, portField, 5, SpringLayout.EAST, portLabel);
            layout.putConstraint(SpringLayout.NORTH, portField, 5, SpringLayout.NORTH, settingsPanel);

            settingsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            tabbedPane.add(settingsPanel, "Settings");
        }

        this.setTitle("PokerServer");
        this.setIconImage(new ImageIcon("images/spades.png").getImage());
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

    private String getIP() {
        return this.ipField.getText();
    }
}
