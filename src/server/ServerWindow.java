package server;

import support.TransportProtocol;

import javax.swing.*;
import java.awt.*;

public class ServerWindow extends JFrame {
    private JTextArea textArea;

    private TextWriter textWriter;
    private MainServerThread mainServerThread;

    public ServerWindow(String title) {
        super(title);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setResizable(false);
        addInterface();
        this.pack();
        this.setVisible(true);
    }

    private void addInterface() {
        addTextPanel();
        addControlPanel();
    }

    private void addTextPanel() {
        JPanel textPanel = new JPanel(true);

        textPanel.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setColumns(50);
        textArea.setRows(30);
        textArea.setEditable(false);
        textArea.setLineWrap(true);

        textWriter = new TextWriter(this.textArea);

        textPanel.add(textArea, BorderLayout.WEST);

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        textPanel.add(scroll, BorderLayout.EAST);

        this.add(textPanel, BorderLayout.NORTH);
    }

    private void addControlPanel() {
        JPanel controlsPanel = new JPanel(true);
        controlsPanel.setLayout(new GridLayout(1, 3));

        JComboBox<Enum> fieldProtocol = new JComboBox<>(TransportProtocol.values());
        JTextField fieldPort = new JTextField();

        JButton startServer = new JButton("Start server");
        startServer.addActionListener(e -> {
            try {
                int port = Integer.parseInt(fieldPort.getText());
                if (port < 0 || port > 50000) {
                    throw new IllegalArgumentException("Invalid port number!");
                }
                TransportProtocol tp;
                if (fieldProtocol.getItemAt(fieldProtocol.getSelectedIndex()) == TransportProtocol.TCP) {
                    tp = TransportProtocol.TCP;
                } else {
                    tp = TransportProtocol.UDP;
                }
                mainServerThread = new MainServerThread(tp, port, textWriter);
                mainServerThread.start();
                textWriter.appendText("Server started by " + tp + " protocol on port " + port);
                startServer.setEnabled(false);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid port number!");
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(this, iae.getMessage());
            }
        });

        controlsPanel.add(fieldProtocol);
        controlsPanel.add(fieldPort);
        controlsPanel.add(startServer);
        this.add(controlsPanel, BorderLayout.SOUTH);
    }
}
