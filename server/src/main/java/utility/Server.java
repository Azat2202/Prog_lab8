package utility;

import main.App;
import exceptions.ConnectionErrorException;
import exceptions.OpeningServerException;
import managers.CommandManager;
import managers.ConnectionManager;
import managers.DatabaseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    private int port;
    private int soTimeout;
    private Printable console;
    private ServerSocketChannel ss;
    private SocketChannel socketChannel;
    private CommandManager commandManager;

    static final Logger serverLogger = LogManager.getLogger(Server.class);

    BufferedInputStream bf = new BufferedInputStream(System.in);
    BufferedReader scanner = new BufferedReader(new InputStreamReader(bf));
    private final DatabaseManager databaseManager;

    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);

    public Server(CommandManager commandManager, DatabaseManager databaseManager) {
        this.port = App.PORT;
        this.soTimeout = App.CONNECTION_TIMEOUT;
        this.console = new BlankConsole();
        this.commandManager = commandManager;
        this.databaseManager = databaseManager;
    }

    public void run(){
        try{
            openServerSocket();
            for(;;){
                try {
                    if (scanner.ready()) {
                        String line = scanner.readLine();
                        if (line.equals("save") || line.equals("s")) {
                            serverLogger.info("Коллекция сохранена");
                        }
                    }
                } catch (IOException ignored) {}
                try{
                    fixedThreadPool.execute(new ConnectionManager(commandManager, connectToClient(), databaseManager));
                } catch (ConnectionErrorException  ignored){}
            }
        } catch (OpeningServerException e) {
            console.printError("Сервер не может быть запущен");
            serverLogger.fatal("Сервер не может быть запущен");
        }
        stop();
        serverLogger.info("Соединение закрыто");
    }

    private void openServerSocket() throws OpeningServerException{
        try {
            SocketAddress socketAddress = new InetSocketAddress(port);
            serverLogger.debug("Создан сокет");
            ss = ServerSocketChannel.open();
            serverLogger.debug("Создан канал");
            ss.bind(socketAddress);
            serverLogger.debug("Открыт канал сокет");
        } catch (IllegalArgumentException exception) {
            console.printError("Порт '" + port + "' находится за пределами возможных значений!");
            serverLogger.error("Порт находится за пределами возможных значений");
            throw new OpeningServerException();
        } catch (IOException exception) {
            serverLogger.error("Произошла ошибка при попытке использовать порт");
            console.printError("Произошла ошибка при попытке использовать порт '" + port + "'!");
            throw new OpeningServerException();
        }
    }

    private SocketChannel connectToClient() throws ConnectionErrorException{
        try {
            console.println("Прослушивание порта '" + port + "'...");
            serverLogger.info("Прослушивание порта '" + port + "'...");
            socketChannel = ss.socket().accept().getChannel();
            console.println("Соединение с клиентом успешно установлено.");
            serverLogger.info("Соединение с клиентом успешно установлено.");
            return socketChannel;
        } catch (IOException exception) {
            serverLogger.fatal("Произошла ошибка при соединении с клиентом!");
            throw new ConnectionErrorException();
        }
    }



    private void stop() {
        class ClosingSocketException extends Exception{}
            try{
                if (socketChannel == null) throw new ClosingSocketException();
                socketChannel.close();
                ss.close();
                serverLogger.info("все соединения закрыты");
            } catch (ClosingSocketException exception) {
                console.printError("Невозможно завершить работу еще не запущенного сервера!");
                serverLogger.fatal("Невозможно завершить работу еще не запущенного сервера!");
            } catch (IOException exception) {
                    console.printError("Произошла ошибка при завершении работы сервера!");
                    serverLogger.fatal("Произошла ошибка при завершении работы сервера!");
            }
    }
}
