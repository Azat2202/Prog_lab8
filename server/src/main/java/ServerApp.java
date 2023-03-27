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
                new Help(commandManager),
                new Info(collectionManager),
                new Show(collectionManager),
                new AddElement(collectionManager),
                new Update(collectionManager),
                new RemoveById(collectionManager),
                new Clear(collectionManager),
//                new Save(fileManager),
//                new Execute(fileManager, commandManager),
                new Exit(),
                new AddIfMax(collectionManager),
                new RemoveGreater(collectionManager),
                new History(commandManager),
                new RemoveAllByAverageMark(collectionManager),
                new CountByAverageMark(collectionManager),
                new CountLessThanExpelledStudents(collectionManager)
        ));
        RequestHandler requestHandler = new RequestHandler(commandManager);
        Server server = new Server(PORT, CONNECTION_TIMEOUT, requestHandler);
        server.run();
    }
}