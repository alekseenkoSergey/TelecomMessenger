package Client;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;

public class ClientWindow extends JFrame {

    private boolean isConnected;
    private Socket socket;
    private DataOutput dataOutput;

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

        JComboBox<String> fieldProtocol = new JComboBox<>(new String[]{"TCP", "UDP"});
        JTextField fieldPort = new JTextField();
        JTextField fieldAddress = new JTextField();

        JButton startListen = new JButton("Connect");
        JButton stopListen = new JButton("Disconnect");
        stopListen.setEnabled(false);
        startListen.addActionListener(e -> {
            try {
                int port = Integer.parseInt(fieldPort.getText());
                socket = new Socket(fieldAddress.getText(), port);
                dataOutput = new DataOutputStream(socket.getOutputStream());
                isConnected = true;
                startListen.setEnabled(false);
                stopListen.setEnabled(true);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid port number!");
            } catch (ConnectException ce){
                JOptionPane.showMessageDialog(this, ce.getMessage());
            } catch (IOException ioex) {
                JOptionPane.showMessageDialog(this, ioex.getMessage());
                ioex.printStackTrace();
            }
        });
        stopListen.addActionListener(e -> {
            isConnected = false;
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            socket = null;
            dataOutput = null;
            startListen.setEnabled(true);
            stopListen.setEnabled(false);
        });

        connectPanel.add(labelProtocol);
        connectPanel.add(labelAddress);
        connectPanel.add(labelPort);
        connectPanel.add(startListen);
        connectPanel.add(fieldProtocol);
        connectPanel.add(fieldAddress);
        connectPanel.add(fieldPort);
        connectPanel.add(stopListen);
        this.add(connectPanel, BorderLayout.NORTH);
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
                try {
                    dataOutput.writeUTF(textField.getText());
                } catch (IOException ioex) {
                    JOptionPane.showMessageDialog(this, "IOExeption =(");
                    ioex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Now you are not connected!");
            }
        });
        buttonPanel.add(send);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }
}
