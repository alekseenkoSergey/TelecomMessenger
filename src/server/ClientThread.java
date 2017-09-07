package server;

import javax.swing.*;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class ClientThread extends Thread {

    private Socket socket;
    private TextWriter textWriter;

    public ClientThread(Socket socket, TextWriter textWriter) {
        this.socket = socket;
        this.textWriter = textWriter;
    }

    @Override
    public void run() {
        try {
            DataInput dataInput = new DataInputStream(socket.getInputStream());
            while (true) {
                if (socket.isConnected()) {
                    textWriter.appendText("Someone say: " + dataInput.readUTF());
                }
            }
        } catch (EOFException eof) {
            textWriter.appendText("One of the client was disconnected");
        } catch (IOException ioex) {
            JOptionPane.showMessageDialog(null, ioex.getMessage());
            ioex.printStackTrace();
        }
    }
}
