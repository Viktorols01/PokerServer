package main;

import javax.swing.JOptionPane;

import bots.MyBot;
import client.BotClient;
import client.PokerClient;
import client.SpectatorClient;
import client.YouClient;
import frames.ServerFrame;
import server.PokerServer;

public class Main {
    public static void main(String[] args) {
        int port;
        while (true) {
            try {
                String stringport = promptString("Select port", "50160");
                port = Integer.valueOf(stringport);
                break;
            } catch (Exception e) {
                continue;
            }
        }

        String[] options = new String[] { "Create a server", "Let a bot play", "Let you play", "Let you watch",
                "Cancel" };
        loop: while (true) {
            String option = promptOption("What is your client going to do?", options);

            PokerClient client;
            PokerServer server;
            switch (option) {
                case "Create a server":
                    server = new PokerServer(port);
                    new ServerFrame(server, 1200, 800);
                    break;
                case "Let a bot play":
                    client = new BotClient(promptString("What is your client going to be named?", "name"), new MyBot(),
                            true);
                    client.connect("localhost", port);
                    break;
                case "Let you play":
                    client = new YouClient(promptString("What is your client going to be named?", "name"), false);
                    client.connect("localhost", port);
                    break;
                case "Let you watch":
                    client = new SpectatorClient(promptString("What is your client going to be named?", "name"), false);
                    client.connect("localhost", port);
                    break;
                case "Cancel":
                    break loop;
                default:
                    break loop;
            }
        }
    }

    public static String promptOption(String message, String[] options) {
        int response = JOptionPane.showOptionDialog(null, message, "Pick an option.",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
        return options[response];
    }

    public static String promptString(String message, String initial) {
        String response = JOptionPane.showInputDialog(message, initial);
        return response;
    }
}