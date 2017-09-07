package client;

import support.TransportProtocol;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class ClientWindow extends JFrame {

    private TransportProtocol tp;
    private int port;

    private boolean isConnected;

    private Socket socket;
    private DataOutput TCPDataOutput;

    private DatagramSocket datagramSocket;
    private InetAddress inetAddress;

    public ClientWindow(String title) {
        super(title);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setResizable(false);
        addInterface();
        this.pack();
        this.setVisible(true);
    }

    private void addInterface() {
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

        JButton connect = new JButton("Connect");
        JButton disconnect = new JButton("Disconnect");
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
                    TCPDataOutput = new DataOutputStream(socket.getOutputStream());
                    connected(connect, disconnect);
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
                    connected(connect, disconnect);
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
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            socket = null;
            TCPDataOutput = null;
            disconnected(connect, disconnect);
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

    private void connected(JButton connect, JButton disconnect) {
        isConnected = true;
        connect.setEnabled(false);
        disconnect.setEnabled(true);
    }

    private void disconnected(JButton connect, JButton disconnect) {
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
            if (tp == TransportProtocol.TCP) {
                if (isConnected) {
                    try {
                        TCPDataOutput.writeUTF(textField.getText());
                    } catch (IOException ioex) {
                        JOptionPane.showMessageDialog(this, "IOExeption =(");
                        ioex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Now you are not connected!");
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
        });
        buttonPanel.add(send);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }
}
