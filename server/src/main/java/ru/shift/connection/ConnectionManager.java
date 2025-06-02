package ru.shift.connection;

import lombok.extern.slf4j.Slf4j;
import ru.shift.exeptions.MessageException;
import ru.shift.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ConnectionManager {
    private final Map<String, Connection> connections = new ConcurrentHashMap<>();

    public boolean addUser(String user, Connection connection) {
        synchronized (connections) {
            if (connections.containsKey(user)) {
                return false;
            }
            connections.put(user, connection);
            return true;
        }
    }

    public void removeUser(String user) {
        synchronized (connections) {
            connections.remove(user);
        }
    }

    public List<String> getAllUsers() {
        return new ArrayList<>(connections.keySet());
    }

    public void sendBroadcastMessage(Message message) {
        connections.values().forEach(connection -> {
            try {
                connection.sendMessage(message);
            } catch (MessageException e) {
                log.error("Failed to send message to client: {}", e.getMessage());
            }
        });
    }
}