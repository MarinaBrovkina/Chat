package ru.shift;

import lombok.extern.slf4j.Slf4j;
import ru.shift.exeptions.MessageException;
import ru.shift.io.MessageReader;
import ru.shift.io.MessageWriter;
import ru.shift.messages.Message;

import java.io.IOException;
import java.net.Socket;

@Slf4j
public class MessageConnection implements AutoCloseable {
    private final Socket socket;
    private final MessageReader reader;
    private final MessageWriter writer;
    private final Thread writerThread;

    public MessageConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new MessageReader(socket);
        this.writer = new MessageWriter(socket);
        this.writerThread = new Thread(writer);
        writerThread.start();
    }

    public void send(Message message) throws MessageException {
        try {
            writer.writeMessage(message);
        } catch (Exception e) {
            throw new MessageException("Ошибка отправки сообщения", e);
        }
    }

    public Message read() throws MessageException {
        try {
            return reader.readMessage();
        } catch (MessageException e) {
            throw e;
        } catch (Exception e) {
            throw new MessageException("Ошибка чтения сообщения", e);
        }
    }

    @Override
    public void close() {
        if (writer != null) {
            writer.stop();
        }

        if (writerThread != null && writerThread.isAlive()) {
            try {
                writerThread.interrupt();
                writerThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Interrupted while waiting for writer thread");
            }
        }

        if (reader != null) {
            try {
                reader.close();
            } catch (Exception e) {
                log.warn("Error closing message reader: {}", e.getMessage());
            }
        }

        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                log.warn("Error closing socket: {}", e.getMessage());
            }
        }
    }

    public boolean isClosed() {
        return socket.isClosed();
    }
}