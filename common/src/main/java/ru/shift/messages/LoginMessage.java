package ru.shift.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginMessage extends Message {

    @JsonProperty
    private String name;

    public LoginMessage(String name) {
        this.name = name;
    }

    @Override
    public MessageType getType() {
        return MessageType.SET_NAME_MSG;
    }
}
