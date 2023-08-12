package test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class TestClient {
    Socket socket;
    public TestClient() {
        this.socket = new Socket();
    }
    
    public void testConnect() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Connect to which IP?");
        String ip = scanner.nextLine();
        System.out.println("Connect to which port?");
        String port = scanner.nextLine();
        scanner.close();

        InetSocketAddress address = new InetSocketAddress("localhost", Integer.valueOf(port));
        try {
            socket.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TestClient client = new TestClient();
        client.testConnect();
    }
}
