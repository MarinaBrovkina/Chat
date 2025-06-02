package ru.shift.controller;

import lombok.extern.slf4j.Slf4j;
import ru.shift.exeptions.MessageException;
import ru.shift.listeners.DisconnectListener;
import ru.shift.listeners.MessageListener;
import ru.shift.messages.*;
import ru.shift.model.Client;
import ru.shift.view.ChatView;
import ru.shift.view.ViewListener;

import javax.swing.*;

@Slf4j
public class ClientController implements ViewListener, DisconnectListener, MessageListener {
    private static final String CONNECTION_ERROR = "Ошибка подключения: ";
    private static final String NO_CONNECTION = "Нет соединения с сервером";
    private static final String NAME_SEND_ERROR = "Ошибка отправки имени: ";
    private static final String MESSAGE_SEND_ERROR = "Ошибка отправки сообщения";
    private static final String DISCONNECTED = "Отключение от сервера";
    private static final String SYSTEM_NAME = "Всеведущий";
    private final Client client;
    private final ChatView view;

    private String pendingName;

    public ClientController() {
        this.client = new Client();
        this.view = new ChatView();

        client.addDisconnectListener(this);
        client.addMessageListener(this);
        view.setListener(this);
        view.showInitialWindows();
    }

    @Override
    public void onConnectRequested(String host, String port) {
        log.info("Connection requested to {}:{}", host, port);
        try {
            client.connect(host, Integer.parseInt(port));
            view.showNameInput();
        } catch (Exception e) {
            view.showError(CONNECTION_ERROR + "Сервер недоступен");
        }
    }

    @Override
    public void onNameSubmitted(String name) {
        log.info("Name submitted: {}", name);
        this.pendingName = name;
        try {
            if (client.isDisconnected()) {
                view.showError(NO_CONNECTION);
                view.showConnectScreen();
                return;
            }
            client.sendMessage(new LoginMessage(name));
        } catch (MessageException e) {
            view.showError(NAME_SEND_ERROR + getErrorMessage(e));
        }
    }

    @Override
    public void onMessageSent(String text) {
        try {
            client.sendMessage(new TextMessage(client.getCurrentUserName(), text));
        } catch (MessageException e) {
            view.showError(MESSAGE_SEND_ERROR + " (" + getErrorMessage(e) + ")");
        }
    }

    @Override
    public void onDisconnect() {
        view.clearChat();
        view.showError(DISCONNECTED);
        view.showConnectScreen();
    }

    private String getErrorMessage(Exception e) {
        return e.getMessage() != null ? e.getMessage() : "неизвестная ошибка";
    }

    @Override
    public void onExitRequested() {
        client.disconnect();
    }

    @Override
    public void onMessage(Message msg) {
        log.debug("Message received: {}", msg.getType());
        SwingUtilities.invokeLater(() -> {
            switch (msg.getType()) {
                case ACCEPT_MSG -> handleAcceptMessage((AcceptMessage) msg);
                case TEXT_MSG -> handleTextMessage((TextMessage) msg);
                case USER_STATE_CHANGED_MSG -> handleUserStateMessage((UserStateChangedMessage) msg);
                default -> handleUnknownMessage(msg);
            }
        });
    }

    private void handleAcceptMessage(AcceptMessage msg) {
        if (msg.isAccepted()) {
            client.setUserName(pendingName);
            view.showChatWindow();
            view.updateUserList(msg.getOnlineUsers());
            view.getNameWindow().dispose();
        } else {
            JOptionPane.showMessageDialog(view.getNameWindow(),
                    msg.getDescription(),
                    "Ошибка имени",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleTextMessage(TextMessage msg) {
        view.addMessage(
                msg.getTimestamp(),
                msg.getUserName(),
                msg.getText()
        );
    }

    private void handleUserStateMessage(UserStateChangedMessage msg) {
        if (msg.leftChat()) {
            view.removeOnlineUser(msg.getName());
            view.addMessage(msg.getTimestamp(), SYSTEM_NAME, msg.getName() + " нас покинул(а). \nГрустим (づ◡﹏◡)づ");
        } else {
            view.addOnlineUser(msg.getName());
            view.addMessage(msg.getTimestamp(), SYSTEM_NAME, msg.getName() + " теперь вместе с нами! \nПриветствуем (づ ◕‿◕ )づ");
        }
    }

    private void handleUnknownMessage(Message msg) {
        System.err.printf("Получено неожиданное сообщение типа: %s. Содержание: %s%n", msg.getType(), msg);
    }
}