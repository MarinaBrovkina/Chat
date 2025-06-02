package ru.shift.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LoginMessage.class),
        @JsonSubTypes.Type(value = TextMessage.class),
        @JsonSubTypes.Type(value = UserStateChangedMessage.class),
        @JsonSubTypes.Type(value = AcceptMessage.class),
        @JsonSubTypes.Type(value = ShutdownMessage.class)
})

public abstract class Message {
    private final Instant timestamp = Instant.now();

    public abstract MessageType getType();

}