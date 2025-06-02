package ru.shift.view;

import lombok.Setter;
import org.apache.commons.text.StringEscapeUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class MainWindow extends JFrame {
    private final JTextArea messagesArea;
    private JLabel usersLabel;
    private final Set<String> onlineUsers = new HashSet<>();
    private final List<String> messageHistory = new ArrayList<>();
    @Setter
    private MessageSenderListener messageListener;

    public MainWindow() {
        super("Чатик ʕ ᵔᴥᵔ ʔ");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        pack();
        setLocationRelativeTo(null);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel usersPanel = createUsersPanel();
        mainPanel.add(usersPanel, BorderLayout.EAST);

        messagesArea = new JTextArea();
        messagesArea.setFont(new Font("Symbola", Font.PLAIN, 12));
        messagesArea.setEditable(false);
        messagesArea.setLineWrap(true);
        messagesArea.setWrapStyleWord(true);
        JScrollPane messagesScroll = new JScrollPane(messagesArea);
        messagesScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainPanel.add(messagesScroll, BorderLayout.CENTER);
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(110, 0));

        JLabel title = new JLabel("Все те, кто с нами");
        title.setFont(new Font(null, Font.BOLD, 12));
        panel.add(title, BorderLayout.NORTH);

        usersLabel = new JLabel();
        usersLabel.setVerticalAlignment(SwingConstants.TOP);
        JScrollPane scrollPane = new JScrollPane(usersLabel);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JTextArea inputArea = new JTextArea();
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setRows(1);
        inputArea.setFont(new Font("Dialog", Font.PLAIN, 12));

        inputArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSize();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSize();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSize();
            }

            private void updateSize() {
                SwingUtilities.invokeLater(() -> {
                    int rows = inputArea.getLineCount();
                    inputArea.setRows(Math.min(Math.max(rows, 1), 5));
                    panel.revalidate();
                });
            }
        });

        JScrollPane scrollPane = new JScrollPane(inputArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JButton sendButton = new JButton("Отправить");
        sendButton.setPreferredSize(new Dimension(110, 26));
        sendButton.addActionListener(e -> sendMessage(inputArea));

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        return panel;
    }

    private void sendMessage(JTextArea inputArea) {
        String text = inputArea.getText().trim();
        if (!text.isEmpty() && messageListener != null) {
            messageListener.send(text);
            inputArea.setText("");
            inputArea.setRows(1);
        }
    }

private static final DateTimeFormatter TIME_FORMATTER =
        DateTimeFormatter.ofPattern("dd MMM HH:mm:ss")
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault());

    public void addNewMessage(Instant timestamp, String user, String text) {
        String safeUser = StringEscapeUtils.escapeHtml4(user);
        String safeText = StringEscapeUtils.escapeHtml4(text);
        String timeStr = TIME_FORMATTER.format(timestamp);

        String formattedMessage = String.format("[%s] %s: %s",
                timeStr, safeUser, safeText);

        messageHistory.add(formattedMessage);
        updateMessagesDisplay();
    }

    public void addOnlineUser(String name) {
        onlineUsers.add(escapeHtml(name));
        updateUsersDisplay();
    }

    public void removeOnlineUser(String name) {
        onlineUsers.remove(escapeHtml(name));
        updateUsersDisplay();
    }

    public void clearOnlineUsers() {
        onlineUsers.clear();
        updateUsersDisplay();
    }

    private void updateMessagesDisplay() {
        SwingUtilities.invokeLater(() -> {
            messagesArea.setText(String.join("\n", messageHistory));
            messagesArea.setCaretPosition(messagesArea.getDocument().getLength());
        });
    }

    private void updateUsersDisplay() {
        SwingUtilities.invokeLater(() -> {
            String usersHtml = onlineUsers.stream()
                    .sorted()
                    .collect(Collectors.joining("<br>", "<html>", "</html>"));
            usersLabel.setText(usersHtml);
        });
    }

    public void clearChat() {
        messageHistory.clear();
        onlineUsers.clear();
        updateMessagesDisplay();
        updateUsersDisplay();
    }

    private String escapeHtml(String input) {
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}