package ru.shift.io;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.shift.JsonMapper;
import ru.shift.exeptions.MessageException;
import ru.shift.messages.Message;
import ru.shift.messages.MessageType;
import ru.shift.messages.ShutdownMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

@Slf4j
public class MessageWriter implements Runnable {
    private final OutputStream outputStream;
    private final ObjectMapper objectMapper;
    private final BoundedMessageQueue queue;
    private static final Message POISON_PILL = new ShutdownMessage();

    public MessageWriter(Socket socket) throws IOException {
        outputStream = socket.getOutputStream();
        objectMapper = JsonMapper.getInstance().configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        queue = new BoundedMessageQueue();
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Message message = queue.take();

                if (message == POISON_PILL || message.getType() == MessageType.SHUTDOWN) {
                    log.info("Received shutdown signal");
                    break;
                }

                writeMessageImpl(message);
            }
        } catch (InterruptedException e) {
            log.info("Writer thread interrupted");
            Thread.currentThread().interrupt();
        } catch (MessageException e) {
            log.error("Failed to send message: {}", e.getMessage());
        } finally {
            closeResources();
        }
    }

    public void writeMessage(Message message) {
        queue.add(message);
    }

    private void writeMessageImpl(Message message) throws MessageException {
        try {
            objectMapper.writeValue(outputStream, message);
        } catch (JsonProcessingException e) {
            throw new MessageException("Ошибка сериализации сообщения", e);
        } catch (IOException e) {
            throw new MessageException("Ошибка записи в поток", e);
        }
    }

    public void stop() {
        queue.add(POISON_PILL);
    }

    private void closeResources() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            log.error("Error closing output stream: {}", e.getMessage());
        }
    }
}
