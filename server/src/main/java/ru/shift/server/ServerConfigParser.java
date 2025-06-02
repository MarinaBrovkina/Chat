package ru.shift.server;

import ru.shift.exceptions.ServerException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerConfigParser {
    private static final String CONFIG_FILE = "server.properties";
    private static final String PORT_PROPERTY = "port";
    private static final int MIN_PORT_NUMBER = 1024;
    private static final int MAX_PORT_NUMBER = 65535;
    private static final String MAX_CONNECTIONS_PROPERTY = "maxConnections";
    private static final String INVALID_PORT_ERROR_MSG =  "Неверный номер порта. Порт должен быть в [%d, %d]."
            .formatted(MIN_PORT_NUMBER, MAX_PORT_NUMBER);
    private final Properties properties;

    public ServerConfigParser() {
        this.properties = loadProperties();
    }

    private Properties loadProperties() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new ServerException("Файл конфигурации не найден: " + CONFIG_FILE);
            }
            prop.load(input);
            return prop;
        } catch (IOException e) {
            throw new ServerException("Ошибка загрузки конфигурации: " + e.getMessage(), e);
        }
    }

    public int getServerPort() throws ServerException {
        String portString = properties.getProperty(PORT_PROPERTY);

        if (portString == null) {
            throw new ServerException("Порт не указан в конфигурации");
        }

        try {
            int port = Integer.parseInt(portString);
            if (port <= MIN_PORT_NUMBER || port >= MAX_PORT_NUMBER) {
                throw new ServerException(port + INVALID_PORT_ERROR_MSG);
            }
            return port;
        } catch (NumberFormatException e) {
            throw new ServerException("Некорректный формат порта: " + portString);
        }
    }

    public int getMaxConnectionsNumber() throws ServerException {
        String maxConnectionsString = properties.getProperty(MAX_CONNECTIONS_PROPERTY);

        if (maxConnectionsString == null) {
            throw new ServerException("Максимальное число подключений не указано");
        }

        try {
            int maxConnections = Integer.parseInt(maxConnectionsString);
            if (maxConnections <= 0) {
                throw new ServerException("Недопустимое количество подключений");
            }
            return maxConnections;
        } catch (NumberFormatException e) {
            throw new ServerException("Некорректный формат числа подключений: " + maxConnectionsString);
        }
    }
}
