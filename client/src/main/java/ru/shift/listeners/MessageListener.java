package ru.shift.listeners;

import ru.shift.messages.Message;

public interface MessageListener {
    void onMessage(Message message);
}
