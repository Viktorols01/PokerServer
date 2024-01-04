package frames;

import java.awt.Dimension;
import java.awt.Font;
import java.net.Inet4Address;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
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
        tabbedPane.setPreferredSize(new Dimension(400, 300));

        {
            JPanel instructionPanel = new JPanel();
            instructionPanel.setLayout(new BoxLayout(instructionPanel, 1));
            JLabel titleLabel = new JLabel("PokerServer");
            titleLabel.setFont(new Font("Serif", Font.PLAIN, 36));
            instructionPanel.add(titleLabel);
            JLabel credLabel = new JLabel("By Viktor Ols");
            credLabel.setFont(new Font("Serif", Font.PLAIN, 12));
            instructionPanel.add(credLabel);
            instructionPanel.add(new JLabel(" "));
            instructionPanel.add(new JLabel("Instructions:"));
            instructionPanel.add(new JLabel("Start off by adjusting your client settings in the settings tab."));
            instructionPanel.add(new JLabel("If you wish to use a bot, you can create one by modifying the file \"PokerServer/bots/MyBot.java\"."));
            instructionPanel.add(new JLabel("For further instructions on bot-making, refer to the file \"PokerServer/BOTMAKING\"."));
            instructionPanel.add(new JLabel("Now simply create or join a server."));
            instructionPanel.add(new JLabel("Make sure to be on a trusted network. Det har fungerat tidigare bror lita på mig tack"));
            tabbedPane.add(instructionPanel, "Instructions");
        }

        {
            JPanel clientPanel = new JPanel();
            clientPanel.setLayout(new BoxLayout(clientPanel, 1));
            JButton botButton = new JButton("Bot play");
            botButton.addActionListener((e) -> {
                PokerClient client = new BotClient("BOT " + getClientName(), new MyBot(),
                        true);
                client.connect(getIP(), getPort());
            });
            clientPanel.add(botButton);
            JButton youButton = new JButton("You play");
            youButton.addActionListener((e) -> {
                PokerClient client = new YouClient("PLAYER " + getClientName(), false);
                client.connect(getIP(), getPort());
            });
            clientPanel.add(youButton);
            JButton spectateButton = new JButton("Spectate");
            spectateButton.addActionListener((e) -> {
                PokerClient client = new SpectatorClient("SPECTATOR " + getClientName(), false);
                client.connect(getIP(), getPort());
            });
            clientPanel.add(spectateButton);
            tabbedPane.add(clientPanel, "Create client");

        }

        {
            JPanel serverPanel = new JPanel();
            serverPanel.setLayout(new BoxLayout(serverPanel, 1));
            try {
                serverPanel.add(new JLabel("IPv4 address: " + Inet4Address.getLocalHost().getHostAddress()));
            } catch (UnknownHostException e) {
                serverPanel.add(new JLabel("IPv4 address: " + "ERROR"));
            }
            JButton serverButton = new JButton("Create server");
            serverButton.addActionListener((e) -> {
                new ServerFrame(new PokerServer(getPort()), 1200, 800);
            });
            serverPanel.add(serverButton);
            tabbedPane.add(serverPanel, "Create server");
        }

        {
            JPanel settingsPanel = new JPanel();
            SpringLayout layout = new SpringLayout();
            settingsPanel.setLayout(layout);
            
            JLabel nameLabel = new JLabel("Name: ");
            nameField = new JTextField("Player");
            nameField.setPreferredSize(new Dimension(200, 25));
            settingsPanel.add(nameLabel);
            layout.putConstraint(SpringLayout.WEST, nameLabel, 5, SpringLayout.WEST, settingsPanel);
            layout.putConstraint(SpringLayout.NORTH, nameLabel, 5, SpringLayout.NORTH, settingsPanel);
            settingsPanel.add(nameField);
            layout.putConstraint(SpringLayout.WEST, nameField, 5, SpringLayout.EAST, nameLabel);
            layout.putConstraint(SpringLayout.NORTH, nameField, 5, SpringLayout.NORTH, settingsPanel);
            
            JLabel portLabel = new JLabel("Port: ");
            portField = new JTextField("50160");
            portField.setPreferredSize(new Dimension(100, 25));
            settingsPanel.add(portLabel);
            layout.putConstraint(SpringLayout.WEST, portLabel, 5, SpringLayout.WEST, settingsPanel);
            layout.putConstraint(SpringLayout.NORTH, portLabel, 5, SpringLayout.SOUTH, nameField);
            settingsPanel.add(portField);
            layout.putConstraint(SpringLayout.WEST, portField, 5, SpringLayout.EAST, portLabel);
            layout.putConstraint(SpringLayout.NORTH, portField, 5, SpringLayout.SOUTH, nameField);

            JLabel ipLabel = new JLabel("IP-address: ");
            ipField = new JTextField("localhost");
            ipField.setPreferredSize(new Dimension(100, 25));
            settingsPanel.add(ipLabel);
            layout.putConstraint(SpringLayout.WEST, ipLabel, 5, SpringLayout.WEST, settingsPanel);
            layout.putConstraint(SpringLayout.NORTH, ipLabel, 5, SpringLayout.SOUTH, portField);
            settingsPanel.add(ipField);
            layout.putConstraint(SpringLayout.WEST, ipField, 5, SpringLayout.EAST, ipLabel);
            layout.putConstraint(SpringLayout.NORTH, ipField, 5, SpringLayout.SOUTH, portField);

            tabbedPane.add(settingsPanel, "Settings");
        }

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
