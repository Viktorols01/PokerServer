package main;

import javax.swing.JOptionPane;

import client.BotClient;
import client.PokerClient;
import client.SpectatorClient;
import client.YouClient;

public class ClientRunner {
    public static void main(String[] args) {
        String[] options = new String[] { "Let a bot play", "Let you play", "Let you watch", "Cancel" };
        loop: while (true) {
            String option = getOption(options);
            
            PokerClient client;
            switch (option) {
                case "Let a bot play":
                    client = new BotClient(getName(), new MyBot(), true);
                    break;
                case "Let you play":
                    client = new YouClient(getName(), false);
                    break;
                case "Let you watch":
                    client = new SpectatorClient(getName(), false);
                    break;
                case "Cancel":
                    break loop;
                default:
                    break loop;
            }
            client.connect("localhost", 50160);
        }
    }

    public static String getOption(String[] options) {
        int response = JOptionPane.showOptionDialog(null, "What is your client going to do?", "Pick an option.",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
        return options[response];
    }

    public static String getName() {
        String response = JOptionPane.showInputDialog("What is your client going to be named?");
        return response;
    }
}
