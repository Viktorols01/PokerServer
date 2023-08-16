package comms;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Connection {
    private String name;
    private Type type;

    private Socket socket;
    private OutputStream outStream;
    private InputStream inStream;
    private OutputStreamWriter outStreamWriter;
    private BufferedWriter bufferedWriter;

    private Scanner scanner;

    public Connection(Socket socket) {
        try {
            this.socket = socket;

            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Connection(InetSocketAddress address) {
        try {
            this.socket = new Socket();
            this.socket.connect(address);

            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Connection(String ip, int port) {
        try {
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress("localhost", port));

            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() throws IOException {
        this.outStream = socket.getOutputStream();
        this.outStreamWriter = new OutputStreamWriter(outStream);
        this.bufferedWriter = new BufferedWriter(outStreamWriter);

        this.inStream = socket.getInputStream();
        this.scanner = new Scanner(inStream);
    }

    public void write(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.write('\n');
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String nextLine() {
        return scanner.nextLine();
    }

    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    public void close() {
        try {
            this.socket.close();
            this.outStream.close();
            this.outStreamWriter.close();
            this.bufferedWriter.close();

            this.inStream.close();
            this.scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return this.socket;
    }

    public String getIP() {
        return this.socket.getInetAddress().getHostAddress();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public enum Type {
        PLAYER,
        SPECTATOR;
    }
}