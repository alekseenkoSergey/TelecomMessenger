package server;

import support.TransportProtocol;

import javax.swing.*;
import java.awt.*;

/* Наследуем наш класс-окно от JFrame чтобы добавлять сюда элементы интерфейса */
public class ServerWindow extends JFrame {

    private TextWriter textWriter;

    public ServerWindow(String title) {
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
        /* Добавляем панель с текстовым полем для вывода текста */
        addTextPanel();
        /* Добавляем панель с полями настройки сервера */
        addControlPanel();
    }

    private void addTextPanel() {
        JPanel textPanel = new JPanel(true);
        textPanel.setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setColumns(50);
        textArea.setRows(30);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        /* Создаем объект, который будет писать в текстовую панель */
        textWriter = new TextWriter(textArea);

        textPanel.add(textArea, BorderLayout.WEST);

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        textPanel.add(scroll, BorderLayout.EAST);

        this.add(textPanel, BorderLayout.NORTH);
    }

    private void addControlPanel() {
        JPanel controlsPanel = new JPanel(true);
        controlsPanel.setLayout(new GridLayout(1, 3));

        // В этом поле будем выбирать транспортный протокол
        JComboBox<Enum> fieldProtocol = new JComboBox<>(TransportProtocol.values());
        // В этом поле будет указывать порт для прослушивания
        JTextField fieldPort = new JTextField();
        // Кнопка для старта сервера по заданым настройкам
        JButton startServer = new JButton("Start server");
        startServer.addActionListener(e -> {
            try {
                // Считываем номер порта
                int port = Integer.parseInt(fieldPort.getText());
                if (port < 0 || port > 50000) {
                    throw new IllegalArgumentException("Invalid port number!");
                }
                // Считываем транспортный протокол
                TransportProtocol tp;
                if (fieldProtocol.getItemAt(fieldProtocol.getSelectedIndex()) == TransportProtocol.TCP) {
                    tp = TransportProtocol.TCP;
                } else {
                    tp = TransportProtocol.UDP;
                }
                /* Создаем главный поток сервера и стартуем его */
                new MainServerThread(tp, port, textWriter).start();
                // Пишем в окно сообщение, что сервер стартанул по указаным настройкам
                textWriter.appendText("Server started by " + tp + " protocol on port " + port);
                startServer.setEnabled(false);
            } catch (NumberFormatException nfe) {
                /* Если поймали NumberFormatException значит в поле порта ввели невалидное число */
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
