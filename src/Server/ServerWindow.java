package Server;

import javax.swing.*;
import java.awt.*;

public class ServerWindow extends JFrame {
    private JTextArea textArea;

    private TextWriter textWriter;

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
        controlsPanel.setLayout(new GridLayout(2, 3));

        JLabel labelProtocol = new JLabel("protocol:");
        labelProtocol.setHorizontalAlignment(JLabel.CENTER);
        JLabel labelPort = new JLabel("port:");
        labelPort.setHorizontalAlignment(JLabel.CENTER);

        JComboBox<String> fieldProtocol = new JComboBox<>(new String[]{"TCP", "UDP"});
        JTextField fieldPort = new JTextField();

        JButton startListen = new JButton("Start listening");
        startListen.addActionListener(e -> {
            try {
                int port = Integer.parseInt(fieldPort.getText());
                new MainServerThread(port, textWriter).start();
                textWriter.appendText("Starting server.");
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid port number!");
                nfe.printStackTrace();
            }
        });
        JButton stopListen = new JButton("Stop listening");
        stopListen.addActionListener(e -> {
            textWriter.appendText("Stop server.");
        });

        controlsPanel.add(labelProtocol);
        controlsPanel.add(labelPort);
        controlsPanel.add(startListen);
        controlsPanel.add(fieldProtocol);
        controlsPanel.add(fieldPort);
        controlsPanel.add(stopListen);
        this.add(controlsPanel, BorderLayout.SOUTH);
    }
}
