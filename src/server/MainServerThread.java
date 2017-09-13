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
        // В зависимости от транспортного протокола стартуем сервер по-разному
        if (tp == TransportProtocol.TCP) {
            // В случае TCP-протокола
            try {
                // Создаем сервер-сокет на указаном порту
                ServerSocket serverSocket = new ServerSocket(port);
                // Получаем ExecutorService для будущего параллельного исполнения 5 клиентских потоков
                ExecutorService executor = Executors.newFixedThreadPool(5);
                // В бесконечном цикле слушаем подключения к серверу
                while (true) {
                    // Создаем Socket для соединения с клиентом
                    Socket client = null;
                    // Ждём подлючения
                    while (client == null) {
                        // Как только подлючение получено записываем нового клиента в подготовленный сокет
                        client = serverSocket.accept();
                    }
                    // Пишем о новом подключении
                    textWriter.appendText("New client connected");
                    // Создаем новый клиентский поток, передавая в него его сокет и объект для возможности писать текст
                    // и отдаем его на исполнение ExecutorService'у
                    executor.execute(new ClientThread(client, textWriter));
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                e.printStackTrace();
            }
        } else {
            // В случае UDP-протокола
            try {
                // Создаем DatagramSocket на указаном порту для прослушки сообщений
                DatagramSocket datagramSocket = new DatagramSocket(port);
                // Создаем DatagramPacket, в который будет помещаться пришедший пакет
                DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);
                // В бесконечном цикле принимаем сообщение в помещаем его в подготовенный пакет
                while (true) {
                    datagramSocket.receive(datagramPacket);
                    // Пишем в окно сервера текст из пакета
                    textWriter.appendText("Someone say: " + new String(datagramPacket.getData()));
                    // Очищаем пакет
                    datagramPacket.setData(new byte[1024]);
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
