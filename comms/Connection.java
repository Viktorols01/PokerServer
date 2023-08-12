package comms;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Connection {
    private Socket socket;
    private OutputStream outStream;
    private InputStream inStream;
    private OutputStreamWriter outStreamWriter;
    private BufferedWriter bufferedWriter;

    private Scanner scanner;
    
    public Connection(Socket socket) {
        this.socket = socket;

        try {
            this.outStream = socket.getOutputStream();
            this.outStreamWriter = new OutputStreamWriter(outStream);
            this.bufferedWriter = new BufferedWriter(outStreamWriter);

            this.inStream = socket.getInputStream();
            this.scanner = new Scanner(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public String receive() {
        return scanner.nextLine();
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
        return socket;
    }
}