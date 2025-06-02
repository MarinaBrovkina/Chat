package ru.shift.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TextMessage extends Message {

    @JsonProperty
    private String text;
    @JsonProperty("name")
    private String userName;

    public TextMessage(String userName, String text) {
        this.userName = userName;
        this.text = text;
    }

    @Override
    public MessageType getType() {
        return MessageType.TEXT_MSG;
    }
}
