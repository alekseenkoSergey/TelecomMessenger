package Server;

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

        JComboBox<String> fieldProtocol = new JComboBox<>(new String[]{"TCP", "UDP"});
        JTextField fieldPort = new JTextField();

        JButton startServer = new JButton("Start server");
        startServer.addActionListener(e -> {
            try {
                int port = Integer.parseInt(fieldPort.getText());
                mainServerThread = new MainServerThread(port, textWriter);
                mainServerThread.start();
                textWriter.appendText("Server started on port " + port);
                startServer.setEnabled(false);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid port number!");
            }
        });

        controlsPanel.add(fieldProtocol);
        controlsPanel.add(fieldPort);
        controlsPanel.add(startServer);
        this.add(controlsPanel, BorderLayout.SOUTH);
    }
}
