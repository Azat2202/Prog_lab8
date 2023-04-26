package managers;

import dtp.Request;
import dtp.Response;
import dtp.ResponseStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.RequestHandler;
import utility.Server;

import java.io.*;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;

public class ConnectionManager implements Runnable{
    private CommandManager commandManager;
    private DatabaseManager databaseManager;
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
    private SocketChannel clientSocket;

    static final Logger connectionManagerLogger = LogManager.getLogger(ConnectionManager.class);

    public ConnectionManager(CommandManager commandManager, SocketChannel clientSocket, DatabaseManager databaseManager) {
        this.commandManager = commandManager;
        this.clientSocket = clientSocket;
        this.databaseManager = databaseManager;
    }

    @Override
    public void run(){
        Request userRequest = null;
        Response responseToUser = null;
        try {
            ObjectInputStream clientReader = new ObjectInputStream(clientSocket.socket().getInputStream());
            ObjectOutputStream clientWriter = new ObjectOutputStream(clientSocket.socket().getOutputStream());
            do {
                userRequest = (Request) clientReader.readObject();
                connectionManagerLogger.info("Получен запрос с командой " + userRequest.getCommandName(), userRequest);
                if(!databaseManager.confirmUser(userRequest.getUser())
                        && !userRequest.getCommandName().equals("register")){
                    connectionManagerLogger.info("Юзер не одобрен");
                    responseToUser = new Response(ResponseStatus.LOGIN_FAILED, "Неверный пользователь!");
                } else{
                    responseToUser = forkJoinPool.invoke(new RequestHandler(commandManager, userRequest));
                }
                connectionManagerLogger.debug(forkJoinPool.toString());
                Response finalResponseToUser = responseToUser;
                cachedThreadPool.submit(() -> {
                    try {
                        connectionManagerLogger.debug(this.cachedThreadPool.toString());
                        clientWriter.writeObject(finalResponseToUser);
                        clientWriter.flush();

                    } catch (IOException e) {
                        connectionManagerLogger.error("Не удалось отправить ответ");
                    }
                }).get();
            } while (true);
        } catch (ClassNotFoundException exception) {
            connectionManagerLogger.fatal("Произошла ошибка при чтении полученных данных!");
        }catch (CancellationException | ExecutionException | InterruptedException exception) {
            connectionManagerLogger.warn("При обработке запроса произошла ошибка многопоточности!");
        } catch (InvalidClassException | NotSerializableException exception) {
            connectionManagerLogger.error("Произошла ошибка при отправке данных на клиент!");
        } catch (IOException exception) {
            if (userRequest == null) {
                connectionManagerLogger.error("Непредвиденный разрыв соединения с клиентом!");
            } else {
                connectionManagerLogger.info("Клиент успешно отключен от сервера!");
            }
        } finally {
            try {
                cachedThreadPool.shutdown();
                clientSocket.close();
            } catch (IOException e) {
                connectionManagerLogger.error("Невозмоюно закрыть соединение ");
            }

        }
    }
}
