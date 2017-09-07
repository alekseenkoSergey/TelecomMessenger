package server;

import support.TransportProtocol;

import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServerThread extends Thread {

    private TransportProtocol tp;
    private int port;
    private TextWriter textWriter;

    public MainServerThread(TransportProtocol tp, int port, TextWriter textWriter) {
        this.tp = tp;
        this.port = port;
        this.textWriter = textWriter;
    }

    @Override
    public void run() {
        if (tp == TransportProtocol.TCP) {
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
        } else {
            try {
                DatagramSocket datagramSocket = new DatagramSocket(port);
                DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);
                while (true) {
                    datagramSocket.receive(datagramPacket);
                    textWriter.appendText(new String(datagramPacket.getData()));
                }
            } catch (SocketException se) {
                JOptionPane.showMessageDialog(null, se.getMessage());
                se.printStackTrace();
            } catch (IOException ioex) {
                JOptionPane.showMessageDialog(null, ioex.getMessage());
                ioex.printStackTrace();
            }
        }
    }
}
