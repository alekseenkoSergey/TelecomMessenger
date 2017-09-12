package server;

import javax.swing.*;

 /* Этот класс пишет текст в свой объект JTextArea, и добавляет пропуск строки */
public class TextWriter {
    private JTextArea textArea;

    public TextWriter(JTextArea textArea) {
        this.textArea = textArea;
    }

    public void appendText(String text) {
        textArea.append(text + "\n");
    }
}
