package ru.shift.view;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;

public class ChatView {
    private final MainWindow mainWindow;
    private final ConnectWindow connectWindow;
    @Getter
    private final NameWindow nameWindow;
    private final ErrorWindow errorWindow;
    @Setter
    private ViewListener listener;

    public ChatView() {
        mainWindow = createMainWindow();
        nameWindow = createNameWindow();
        connectWindow = createConnectWindow();
        errorWindow = createErrorWindow();
    }

    public void showInitialWindows() {
        SwingUtilities.invokeLater(() -> {
            mainWindow.setVisible(false);
            connectWindow.setVisible(true);
            nameWindow.setVisible(false);
            errorWindow.setVisible(false);
        });
    }

    public void showConnectScreen() {
        SwingUtilities.invokeLater(() -> {
            connectWindow.setVisible(true);
            nameWindow.setVisible(false);
        });
    }

    public void showNameInput() {
        SwingUtilities.invokeLater(() -> nameWindow.setVisible(true));
    }

    public void showChatWindow() {
        SwingUtilities.invokeLater(() -> {
            connectWindow.setVisible(false);
            nameWindow.setVisible(false);
            mainWindow.setVisible(true);
        });
    }

    public void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            errorWindow.setErrorMessage(message);
            errorWindow.setVisible(true);
        });
    }

    public void addMessage(Instant timestamp, String user, String text) {
        SwingUtilities.invokeLater(() ->
                mainWindow.addNewMessage(timestamp, user, text)
        );
    }

    public void updateUserList(Iterable<String> users) {
        SwingUtilities.invokeLater(() -> {
            mainWindow.clearOnlineUsers();
            users.forEach(mainWindow::addOnlineUser);
        });
    }

    public void addOnlineUser(String name) {
        SwingUtilities.invokeLater(() -> mainWindow.addOnlineUser(name));
    }
    public void clearChat() {
        SwingUtilities.invokeLater(mainWindow::clearChat);
    }

    private MainWindow createMainWindow() {
        MainWindow window = new MainWindow();
        window.setSize(new Dimension(500, 500));
        window.setLocationRelativeTo(null);
        window.setMessageListener(text -> {
            if (listener != null) {
                listener.onMessageSent(text);
            }
        });
        return window;
    }

    private NameWindow createNameWindow() {
        NameWindow window = new NameWindow(mainWindow);
        window.setNameListener(name -> {
            if (listener != null) {
                listener.onNameSubmitted(name);
            }
        });
        window.setExitListener(e -> {
            if (listener != null) {
                listener.onExitRequested();
            }
        });
        return window;
    }

    private ConnectWindow createConnectWindow() {
        ConnectWindow window = new ConnectWindow(mainWindow);
        window.setConnectActionListener(e -> {
            if (listener != null) {
                listener.onConnectRequested(
                        window.getHost(),
                        window.getPort()
                );
            }
        });
        window.setCancelActionListener(e -> {
            window.dispose();
            if (listener != null) {
                listener.onExitRequested();
            }
        });
        return window;
    }

    public void removeOnlineUser(String name) {
        SwingUtilities.invokeLater(() -> mainWindow.removeOnlineUser(name));
    }

    private ErrorWindow createErrorWindow() {
        return new ErrorWindow(mainWindow);
    }
}