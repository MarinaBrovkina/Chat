package ru.shift.messages;

public class ShutdownMessage extends Message {

    @Override
    public MessageType getType() {
        return MessageType.SHUTDOWN;
    }
}