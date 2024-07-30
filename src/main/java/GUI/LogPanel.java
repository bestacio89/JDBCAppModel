package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogPanel extends JPanel {
    private JTextArea logArea;

    public LogPanel() {
        setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);

        // Configure logger
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.ALL);
        logger.addHandler(new TextAreaHandler(logArea));
    }

    private static class TextAreaHandler extends Handler {
        private JTextArea textArea;

        public TextAreaHandler(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void publish(LogRecord record) {
            SwingUtilities.invokeLater(() -> textArea.append(record.getLevel() + ": " + record.getMessage() + "\n"));
        }

        @Override
        public void flush() {}

        @Override
        public void close() throws SecurityException {}
    }
}
