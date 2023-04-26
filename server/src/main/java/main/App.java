package main;

import exceptions.ExitObliged;
import managers.*;

import utility.*;
import commands.*;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App extends Thread {
    //-------------------------------КОНФИГУРАЦИОННЫЕ ПЕРМЕННЫЕ-----------------------------------------
    public static int PORT = 6086;
    public static final int CONNECTION_TIMEOUT = 60 * 1000;

    public static final String DATABASE_URL = "jdbc:postgresql://localhost:1256/studs";
    public static final String DATABASE_URL_HELIOS = "jdbc:postgresql://pg:1256/studs";
    public static final String DATABASE_CONFIG_PATH = "C:\\Users\\R1300-W-12-Stud\\IdeaProjects\\Prog_lab7\\server\\dbconfig.cfg";

    //--------------------------------------------------------------------------------------------------

    private static final Printable console = new BlankConsole();
    static final Logger rootLogger = LogManager.getRootLogger();

    public static void main(String[] args) {
        rootLogger.info("--------------------------------------------------------------------");
        rootLogger.info("----------------------ЗАПУСК СЕРВЕРА--------------------------------");
        rootLogger.info("--------------------------------------------------------------------");
        if(args.length != 0){
            try{
                PORT = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }

        CollectionManager collectionManager = new CollectionManager();

        CommandManager commandManager = new CommandManager(DatabaseHandler.getDatabaseManager());
        commandManager.addCommand(List.of(
                new History(commandManager),
                new Help(commandManager),
                new ExecuteScript(),
                new Exit(),
                new Sleep(),
                new Ping(),
                new Register(DatabaseHandler.getDatabaseManager()),
                new Info(collectionManager),
                new Show(collectionManager),
                new AddElement(collectionManager),
                new Update(collectionManager),
                new RemoveById(collectionManager),
                new Clear(collectionManager),
                new AddIfMax(collectionManager),
                new RemoveGreater(collectionManager),
                new RemoveAllByAverageMark(collectionManager),
                new CountByAverageMark(collectionManager),
                new CountLessThanExpelledStudents(collectionManager)
        ));
        Server server = new Server(commandManager, DatabaseHandler.getDatabaseManager());
        server.run();
    }
}