import managers.*;

import java.net.*;
import java.io.*;
import java.util.List;

import commands.*;
import managers.*;
import utilty.RequestHandler;
import utilty.Server;

public class App extends Thread {
    public static final int PORT = 1821;
    public static final int CONNECTION_TIMEOUT = 60 * 1000;

    public static void main(String[] args) {
        CollectionManager collectionManager = new CollectionManager();
        CommandManager commandManager = new CommandManager();
//        commandManager.addCommand(List.of(
//                new Help(commandManager),
//                new Info( collectionManager),
//                new Show(collectionManager),
//                new AddElement(console, collectionManager),
//                new Update(console, collectionManager),
//                new RemoveById(console, collectionManager),
//                new Clear(console, collectionManager),
//                new Save(console, fileManager),
//                new Execute(console, fileManager, commandManager),
//                new Exit(),
//                new AddIfMax(console, collectionManager),
//                new RemoveGreater(console, collectionManager),
//                new History(console, commandManager),
//                new RemoveAllByAverageMark(console, collectionManager),
//                new CountByAverageMark(console, collectionManager),
//                new CountLessThanExpelledStudents(console, collectionManager)
//        ));
        RequestHandler requestHandler = new RequestHandler(commandManager);
        Server server = new Server(PORT, CONNECTION_TIMEOUT, requestHandler);
        server.run();
    }
}