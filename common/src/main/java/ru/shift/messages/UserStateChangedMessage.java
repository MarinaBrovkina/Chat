package ru.shift.messages;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserStateChangedMessage extends Message {

    private boolean leftChat;
    private String name;

    public UserStateChangedMessage(boolean leftChat, String name) {
        this.leftChat = leftChat;
        this.name = name;
    }

    public boolean leftChat() {
        return leftChat;
    }

    @Override
    public MessageType getType() {
        return MessageType.USER_STATE_CHANGED_MSG;
    }
}
