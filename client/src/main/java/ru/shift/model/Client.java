package ru.shift.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.shift.MessageConnection;
import ru.shift.exeptions.MessageException;
import ru.shift.listeners.DisconnectListener;
import ru.shift.listeners.MessageListener;
import ru.shift.messages.Message;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Client {
    private static final int TIMEOUT = 500;
    @Getter
    private volatile String currentUserName;
    private final List<MessageListener> messageListeners = new CopyOnWriteArrayList<>();
    private final List<DisconnectListener> disconnectListeners = new CopyOnWriteArrayList<>();
    private MessageConnection messageConnection;
    private ExecutorService executor;
    private volatile boolean connectionActive = false;

    public void connect(String host, int port) throws MessageException {
        log.info("Connecting to {}:{}", host, port);
        try {
            Socket socket = new Socket(host, port);
            this.messageConnection = new MessageConnection(socket);
            this.connectionActive = true;
            this.executor = Executors.newSingleThreadExecutor();
            executor.submit(this::readMessages);
            log.debug("Connected successfully");
        } catch (IOException e) {
            throw new MessageException("Ошибка подключения: " + e.getMessage(), e);
        }
    }

    private void readMessages() {
        try {
            while (connectionActive) {
                Message message = messageConnection.read();
                notifyMessageListeners(message);
            }
        } catch (MessageException e) {
            if (connectionActive) {
                log.error("Connection error: {}", e.getMessage());
                disconnect();
            }
        }
    }

    public void sendMessage(Message message) throws MessageException {
        if (!connectionActive) {
            throw new MessageException("Соединение не активно");
        }
        try {
            log.debug("Sending message: {}", message.getType());
            messageConnection.send(message);
        } catch (MessageException e) {
            log.error("Failed to send message: {}", e.getMessage());
            throw e;
        }
    }

    public void disconnect() {
        if (!connectionActive) {
            return;
        }
        connectionActive = false;

        log.info("Disconnecting...");

        try {
            if (executor != null) {
                executor.shutdown();
                if (!executor.awaitTermination(TIMEOUT, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            throw new MessageException("Ошибка при отключении", e);
        } finally {
            if (messageConnection != null) {
                messageConnection.close();
            }
            notifyDisconnectListeners();
            log.info("Disconnected successfully");
        }
    }

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    public void addDisconnectListener(DisconnectListener listener) {
        disconnectListeners.add(listener);
    }

    private void notifyMessageListeners(Message message) {
        messageListeners.forEach(listener -> listener.onMessage(message));
    }

    private void notifyDisconnectListeners() {
        disconnectListeners.forEach(DisconnectListener::onDisconnect);
    }

    public boolean isDisconnected() {
        return messageConnection == null || messageConnection.isClosed();
    }

    public void setUserName(String name) {
        this.currentUserName = name;
    }
}