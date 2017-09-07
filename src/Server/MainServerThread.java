package Server;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServerThread extends Thread {

    private int port;
    private TextWriter textWriter;

    public MainServerThread(int port, TextWriter textWriter) {
        this.port = port;
        this.textWriter = textWriter;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            ExecutorService executor = Executors.newFixedThreadPool(5);
            while (true) {
                Socket client = null;
                while (client == null) {
                    client = serverSocket.accept();
                }
                textWriter.appendText("New client connected");
                executor.execute(new ClientThread(client, textWriter));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            e.printStackTrace();
        }
    }
}
