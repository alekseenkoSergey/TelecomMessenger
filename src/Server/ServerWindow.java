package Server;

import javax.swing.*;
import java.awt.*;

public class ServerWindow extends JFrame {
    private JTextArea textArea;
    private JButton button;

    private TextWriter textFieldWriter;

    public ServerWindow(String title)  {
        super(title);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setResizable(false);
        addControls();
        this.pack();
        this.setVisible(true);

        textArea.append("Starting server...\n");

        //new MainServerThread(textFieldWriter).start();
    }

    private void addControls() {

        // up
        JPanel panel = new JPanel(true);

        panel.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setColumns(50);
        textArea.setRows(30);
        textArea.setEditable(false);
        textArea.setLineWrap(true);

        textFieldWriter = new TextWriter(this.textArea);

        panel.add(textArea, BorderLayout.WEST);

        JScrollPane scroll = new JScrollPane (textArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scroll, BorderLayout.EAST);

        this.add(panel, BorderLayout.NORTH);

        // down
        JPanel panel2 = new JPanel(true);
        panel2.setLayout(new BorderLayout());

        JComboBox<String> protocol = new JComboBox<>(new String[]{"TCP", "UDP"});
        JTextField port = new JTextField();
        JButton startListen = new JButton();
        JButton stopListen = new JButton();
        panel2.add(protocol);
        panel2.add(port);
        panel2.add(startListen);
        panel2.add(stopListen);

        this.add(panel2, BorderLayout.SOUTH);
    }
}
