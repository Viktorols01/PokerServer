import java.util.Scanner;

import poker.PokerServer;

class Main {
    public static void main(String[] args) {
        PokerServer server = new PokerServer();
        server.openConnections();
        
        Scanner scanner = new Scanner(System.in);
        loop:
        while (true) {
            switch (scanner.nextLine().toUpperCase()) {
                case "PRINT":
                    System.out.println(server);    
                    break;
                case "OPEN":
                    server.openConnections();
                    break;
                case "CLOSE":
                    server.closeConnections();
                    break;
                case "START":
                    server.start();
                    break;
                case "EXIT":
                    break loop;
            }
        }
        scanner.close();
    }
}