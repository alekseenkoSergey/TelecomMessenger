package Server;

import javax.swing.*;
import java.io.DataInput;
import java.io.DataInputStream;
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
                if (!socket.isClosed()) {
                    textWriter.appendText(dataInput.readUTF());
                }
            }
        } catch (IOException ioex) {
            JOptionPane.showMessageDialog(null, "IOExeption =(");
            ioex.printStackTrace();
        }
    }
}
