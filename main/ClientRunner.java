package main;

import java.util.Scanner;

import client.PokerClient;
import client.PokerGUI;
import poker.PokerModel;

public class ClientRunner {
    public static void main(String[] args) {
        // Client botClient = new Client(true, false);
        // botClient.connect("localhost", 50160);
        // botClient.start();

        PokerClient client = new PokerClient(true) {

            Scanner scanner;

            @Override
            protected void setup() {
            }

            @Override
            protected String[] getName() {
                return new String[] { "Viktor" };
            }

            @Override
            protected String[] getType() {
                return new String[] { "player" };
            }

            @Override
            protected String[] getMove(PokerModel model) {
                scanner = new Scanner(System.in);
                System.out.println("Make a move:");
                String input = scanner.nextLine();
                String[] split = input.split(" ");
                String move = split[0].toLowerCase();
                String n;
                if (split.length > 1) {
                    n = split[1];
                } else {
                    n = "0";
                }
                return new String[] { move, n };
            }

            @Override
            protected void display(PokerModel model) {
                System.out.println(model);
            }

            @Override
            protected void parseMessage(String message) {
                System.out.println(message);
            }

        };
        client.connect("localhost", 50160);
        client.start();

        // new PokerGUI(client);
    }
}