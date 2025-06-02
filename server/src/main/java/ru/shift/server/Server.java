package ru.shift.server;

import lombok.extern.slf4j.Slf4j;
import ru.shift.connection.Connection;
import ru.shift.connection.ConnectionManager;
import ru.shift.exceptions.ServerException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class Server {
    private final ServerSocket serverSocket;
    private final ConnectionManager connectionManager;

    public Server(int port, int maxConnectionsNumber) throws ServerException {
        try {
            serverSocket = new ServerSocket(port, maxConnectionsNumber);
            connectionManager = new ConnectionManager();
        } catch (IOException e) {
            throw new ServerException(e.getMessage());
        }
    }

    public void start() throws ServerException {
        log.info("Server started on port {}", serverSocket.getLocalPort());
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                log.info("New client connected: {}", clientSocket.getRemoteSocketAddress());
                Connection connection = new Connection(clientSocket, connectionManager);
                new Thread(connection).start();
            } catch (IOException e) {
                throw new ServerException("Ошибка сервера: " + e);
            }
        }
    }
}