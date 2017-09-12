package server;

import javax.swing.*;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

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
            // Получаем из сокета InputStream и оборачиваем его в DataInputStream для комфортной передачи текста
            DataInput dataInput = new DataInputStream(socket.getInputStream());
            // В бесконечном цикле читаем текст из стрима и пишем его в главное окно сервера
            while (true) {
                if (socket.isConnected()) {
                    textWriter.appendText("Someone say: " + dataInput.readUTF());
                }
            }
        } catch (EOFException | SocketException ex) {
            // В случае если мы наткнулись на конец передачи или оборвалось соединение, значит клиент отсоединился
            textWriter.appendText("One of the client was disconnected");
        } catch (IOException ioex) {
            JOptionPane.showMessageDialog(null, ioex.getMessage());
            ioex.printStackTrace();
        }
    }
}
