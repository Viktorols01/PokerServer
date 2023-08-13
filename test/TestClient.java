package test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TestClient {
    Socket socket;
    public TestClient() {
        this.socket = new Socket();
    }
    
    public void testConnect() {
        int port = 50160;

        InetSocketAddress address = new InetSocketAddress("localhost", port);
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
