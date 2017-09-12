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
        // Добавляем
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
        connect.addActionListener(e -> {
            try {
                port = Integer.parseInt(fieldPort.getText());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid port number!");
            }
            if (fieldProtocol.getItemAt(fieldProtocol.getSelectedIndex()) == TransportProtocol.TCP) {
                tp = TransportProtocol.TCP;
            } else {
                tp = TransportProtocol.UDP;
            }

            if (tp == TransportProtocol.TCP) {
                try {
                    socket = new Socket(fieldAddress.getText(), port);
                    dataOutput = new DataOutputStream(socket.getOutputStream());
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
                try {
                    inetAddress = InetAddress.getByName(fieldAddress.getText());
                    datagramSocket = new DatagramSocket();
                    switchToConnectedMode();
                } catch (UnknownHostException uhe) {
                    JOptionPane.showMessageDialog(this, uhe.getMessage());
                    uhe.printStackTrace();
                } catch (SocketException se) {
                    JOptionPane.showMessageDialog(this, se.getMessage());
                    se.printStackTrace();
                }
            }
        });
        disconnect.addActionListener(e -> {
            if (tp == TransportProtocol.TCP) {
                try {
                    socket.close();
                } catch (IOException ioex) {
                    ioex.printStackTrace();
                }
                socket = null;
                dataOutput = null;
            } else {
                datagramSocket.close();
                datagramSocket = null;
                inetAddress = null;
            }
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
        send.addActionListener(e -> {
            if (isConnected) {
                if (tp == TransportProtocol.TCP) {
                    if (!socket.isOutputShutdown()) { // fixme
                        try {
                            dataOutput.writeUTF(textField.getText());
                        } catch (IOException ioex) {
                            JOptionPane.showMessageDialog(this, "IOExeption =(");
                            ioex.printStackTrace();
                        }
                    } else {
                        switchToDisconnectedMode();
                    }
                } else {
                    DatagramPacket datagramPacket = new DatagramPacket(textField.getText().getBytes(), textField.getText().length(), inetAddress, port);
                    try {
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
