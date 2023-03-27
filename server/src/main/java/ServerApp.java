import managers.*;

import utilty.RequestHandler;
import utilty.Server;
import commands.*;

import java.util.List;

public class ServerApp extends Thread {
    public static final int PORT = 6086;
    public static final int CONNECTION_TIMEOUT = 60 * 1000;

    public static void main(String[] args) {
        CollectionManager collectionManager = new CollectionManager();
        CommandManager commandManager = new CommandManager();
        commandManager.addCommand(List.of(
                new AddElement(collectionManager),
                new Help(commandManager)
        ));
        RequestHandler requestHandler = new RequestHandler(commandManager);
        Server server = new Server(PORT, CONNECTION_TIMEOUT, requestHandler);
        server.run();
    }
}