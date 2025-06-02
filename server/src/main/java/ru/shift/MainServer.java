package ru.shift;

import lombok.extern.slf4j.Slf4j;
import ru.shift.exceptions.ServerException;
import ru.shift.exeptions.MessageException;
import ru.shift.server.Server;
import ru.shift.server.ServerConfigParser;

@Slf4j
public class MainServer {

    public static void main(String[] args) {
        try {
            ServerConfigParser serverConfigParser = new ServerConfigParser();
            Server server = new Server(serverConfigParser.getServerPort(),
                    serverConfigParser.getMaxConnectionsNumber());
            server.start();
        } catch (ServerException e) {
            log.error("Server error: {}", e.getMessage(), e);
        } catch (MessageException e) {
            log.error("Message handling error: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Critical server error: {}", e.getMessage(), e);
        }
    }
}
