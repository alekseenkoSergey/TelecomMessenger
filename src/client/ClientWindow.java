package client;

import support.TransportProtocol;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

/* Наследуем наш класс-окно от JFrame чтобы добавлять сюда элементы интерфейса */
public class ClientWindow extends JFrame {

    private TransportProtocol tp;
    private int port;

    private boolean isConnected;

    // Переменные для передачи текста по TCP протоколу
    private Socket socket;
    private DataOutput dataOutput;

    // Переменные для передачи текста по UDP протоколу
    private DatagramSocket datagramSocket;
    private InetAddress inetAddress;

    private JButton connect;
    private JButton disconnect;

    public ClientWindow(String title) {
        /* Настраиваем окно интерфейса */
        super(title);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setResizable(false);
        addInterface();
        this.pack();
        this.setVisible(true);
    }

    private void addInterface() {
        // Добавляем элементы интерфейса
        addConnectPanel();
        addMessagePanels();
    }

    private void addConnectPanel() {
        JPanel connectPanel = new JPanel(true);
        connectPanel.setLayout(new GridLayout(2, 4));

        JLabel labelProtocol = new JLabel("protocol:");
        labelProtocol.setHorizontalAlignment(JLabel.CENTER);
        JLabel labelAddress = new JLabel("address:");
        labelAddress.setHorizontalAlignment(JLabel.CENTER);
        JLabel labelPort = new JLabel("port:");
        labelPort.setHorizontalAlignment(JLabel.CENTER);

        JComboBox<Enum> fieldProtocol = new JComboBox<>(TransportProtocol.values());
        JTextField fieldPort = new JTextField();
        JTextField fieldAddress = new JTextField();

        connect = new JButton("Connect");
        disconnect = new JButton("Disconnect");
        disconnect.setEnabled(false);
        // Действия на кнопку Connect
        connect.addActionListener(e -> {
            // Считываем номер порта
            try {
                port = Integer.parseInt(fieldPort.getText());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid port number!");
            }
            // Считываем транспортный протокол
            if (fieldProtocol.getItemAt(fieldProtocol.getSelectedIndex()) == TransportProtocol.TCP) {
                tp = TransportProtocol.TCP;
            } else {
                tp = TransportProtocol.UDP;
            }
            if (tp == TransportProtocol.TCP) {
                // В случае TCP-протокола
                try {
                    // Открываем новый сокет по указанным настройкам
                    socket = new Socket(fieldAddress.getText(), port);
                    // Берем с него outputStream и оборачиваем его в DateOutputStream для комфортной передачи текста
                    dataOutput = new DataOutputStream(socket.getOutputStream());
                    // Переключаем интерфейс в состояние "соединнен"
                    switchToConnectedMode();
                } catch (IllegalArgumentException iae) {
                    JOptionPane.showMessageDialog(this, iae.getMessage());
                    iae.printStackTrace();
                } catch (ConnectException ce) {
                    JOptionPane.showMessageDialog(this, ce.getMessage());
                } catch (IOException ioex) {
                    JOptionPane.showMessageDialog(this, ioex.getMessage());
                    ioex.printStackTrace();
                }
            } else {
                // В случае UDP-протокола
                try {
                    // Записываем адресс для посылки пакета
                    inetAddress = InetAddress.getByName(fieldAddress.getText());
                    // Создаем новый DatagramSocket
                    datagramSocket = new DatagramSocket();
                    /* Переключаем интерфейс в состояние "соединнен"
                       Хотя на самом деле никакого "соединения" не установлено, мы просто запомнили адрес,
                       куда будем посылать пакеты и создали DatagramSocket. */
                    switchToConnectedMode();
                } catch (UnknownHostException | SocketException uhe) {
                    JOptionPane.showMessageDialog(this, uhe.getMessage());
                    uhe.printStackTrace();
                }
            }
        });
        // Действия на кнопку Disconnect
        disconnect.addActionListener(e -> {
            // Закрываем сокет, обнуляем переменные
            if (tp == TransportProtocol.TCP) {
                // В случае TCP-протокола
                try {
                    socket.close();
                } catch (IOException ioex) {
                    ioex.printStackTrace();
                }
                socket = null;
                dataOutput = null;
            } else {
                // В случае UDP-протокола
                datagramSocket.close();
                datagramSocket = null;
                inetAddress = null;
            }
            // Переключаем интерфейс в состояние "отсоединен"
            switchToDisconnectedMode();
        });

        connectPanel.add(labelProtocol);
        connectPanel.add(labelAddress);
        connectPanel.add(labelPort);
        connectPanel.add(connect);
        connectPanel.add(fieldProtocol);
        connectPanel.add(fieldAddress);
        connectPanel.add(fieldPort);
        connectPanel.add(disconnect);
        this.add(connectPanel, BorderLayout.NORTH);
    }

    private void switchToConnectedMode() {
        isConnected = true;
        connect.setEnabled(false);
        disconnect.setEnabled(true);
    }

    private void switchToDisconnectedMode() {
        isConnected = false;
        connect.setEnabled(true);
        disconnect.setEnabled(false);
    }

    private void addMessagePanels() {
        JPanel textPanel = new JPanel(true);
        textPanel.setLayout(new BorderLayout());
        JLabel labelMessage = new JLabel("Your message:");
        labelMessage.setHorizontalAlignment(JLabel.CENTER);
        textPanel.add(labelMessage, BorderLayout.NORTH);
        JTextField textField = new JTextField();
        textPanel.add(textField, BorderLayout.SOUTH);
        this.add(textPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(true);
        buttonPanel.setLayout(new GridBagLayout());
        JButton send = new JButton("Send");
        // Действия на кнопку Send
        send.addActionListener(e -> {
            // Проверяем флаг подключены ли мы вообще
            if (isConnected) {
                if (tp == TransportProtocol.TCP) {
                    // В случае TCP-протокола
                    if (!socket.isOutputShutdown()) {
                        try {
                            // Если поток вывода не закрыт пишем в него текст из поля textField
                            dataOutput.writeUTF(textField.getText());
                        } catch (IOException ioex) {
                            JOptionPane.showMessageDialog(this, ioex.getMessage());
                            ioex.printStackTrace();
                        }
                    } else {
                        switchToDisconnectedMode();
                    }
                } else {
                    // В случае UDP-протокола
                    // Формируем DatagramPacket, в который запихиваем наш текст в байтовом представлении, указываем адрес отправки и порт
                    DatagramPacket datagramPacket = new DatagramPacket(textField.getText().getBytes(), textField.getText().length(), inetAddress, port);
                    try {
                        // Отправляем через созданный ранее сокет
                        datagramSocket.send(datagramPacket);
                    } catch (IOException ioex) {
                        JOptionPane.showMessageDialog(this, ioex.getMessage());
                        ioex.printStackTrace();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Now you are not connected!");
            }
        });
        buttonPanel.add(send);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }
}
