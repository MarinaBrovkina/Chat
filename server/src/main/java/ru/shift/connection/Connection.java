package ru.shift.connection;

import lombok.extern.slf4j.Slf4j;
import ru.shift.*;
import ru.shift.exeptions.MessageException;
import ru.shift.messages.AcceptMessage;
import ru.shift.messages.LoginMessage;
import ru.shift.messages.Message;
import ru.shift.messages.UserStateChangedMessage;

import java.io.IOException;
import java.net.Socket;

@Slf4j
public class Connection implements Runnable {
    private final ConnectionManager connectionManager;
    private static final String NAME_TAKEN_MSG = "Это имя уже занято";
    private static final String INVALID_NAME = "Некорректное имя пользователя";
    private final MessageConnection messageConnection;
    private String name;

    public Connection(Socket socket, ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        try {
            this.messageConnection = new MessageConnection(socket);
        } catch (IOException e) {
            throw new MessageException("Failed to initialize connection: " + e.getMessage(), e);
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = messageConnection.read();
                switch (message.getType()) {
                    case SET_NAME_MSG -> processNameMessage((LoginMessage) message);
                    case TEXT_MSG -> connectionManager.sendBroadcastMessage(message);
                    default -> log.warn("Unsupported message type: {}", message.getType());
                }
            }
        } catch (MessageException e) {
            log.error("Message processing error: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
        } finally {
            disconnectClient();
        }
    }

    private void processNameMessage(LoginMessage message) {
        String newName = message.getName().trim();
        if (UsernameValidator.isInvalid(newName)) {
            sendMessage(new AcceptMessage(false, INVALID_NAME, null));
            return;
        }

        if (!connectionManager.addUser(newName, this)) {
            sendMessage(new AcceptMessage(false, NAME_TAKEN_MSG, null));
            return;
        }

        this.name = newName;
        sendMessage(new AcceptMessage(true, null, connectionManager.getAllUsers()));
        connectionManager.sendBroadcastMessage(new UserStateChangedMessage(false, newName));
    }

    public void disconnectClient() {
        if (name != null) {
            connectionManager.removeUser(name);
            connectionManager.sendBroadcastMessage(new UserStateChangedMessage(true, name));
        }
        messageConnection.close();
    }

    public void sendMessage(Message message) {
        try {
            messageConnection.send(message);
        } catch (MessageException e) {
            log.error("Failed to send message: {}", e.getMessage());
            disconnectClient();
        }
    }
}