package ru.shift.messages;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class AcceptMessage extends Message {
    private boolean accepted;
    private String description;
    private List<String> onlineUsers;

    public AcceptMessage(boolean accepted, String description, List<String> onlineUsers) {
        this.accepted = accepted;
        this.description = description;
        this.onlineUsers = onlineUsers;
    }

    @Override
    public MessageType getType() {
        return MessageType.ACCEPT_MSG;
    }
}
