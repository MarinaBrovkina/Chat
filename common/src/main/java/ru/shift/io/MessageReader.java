package ru.shift.io;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.shift.JsonMapper;
import ru.shift.exeptions.MessageException;
import ru.shift.messages.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

@Slf4j
public class MessageReader {
    private final InputStream inputStream;
    private final ObjectMapper objectMapper;
    private static final int MAX_MESSAGE_lENGTH = 1024;
    private volatile boolean closed = false;

    public MessageReader(Socket socket) throws IOException {
        this.inputStream = socket.getInputStream();
        this.objectMapper = JsonMapper.getInstance()
                .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
    }

    public Message readMessage() throws MessageException {
        try {
            objectMapper.getFactory().setStreamReadConstraints(
                    StreamReadConstraints.builder()
                            .maxStringLength(MAX_MESSAGE_lENGTH)
                            .build()
            );
            return objectMapper.readValue(inputStream, Message.class);
        } catch (JsonProcessingException e) {
            throw new MessageException("Не валидный JSON", e);
        } catch (IOException e) {
            if (!closed) {
                log.error("Connection error", e);
            }
            throw new MessageException("Ошибка подключения: " + e.getMessage(), e);
        }
    }

    public void close() {
        closed = true;
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            log.warn("Error closing input stream", e);
        }
    }
}