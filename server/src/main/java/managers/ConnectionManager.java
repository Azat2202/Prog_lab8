package managers;

import dtp.Request;
import dtp.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.RequestHandler;
import utility.Server;

import java.io.*;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;

public class ConnectionManager implements Runnable{
    private CommandManager commandManager;
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
    private SocketChannel clientSocket;

    static final Logger connectionManagerLogger = LogManager.getLogger(Server.class);

    public ConnectionManager(CommandManager commandManager, SocketChannel clientSocket) {
        this.commandManager = commandManager;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run(){
        Request userRequest = null;
        Response responseToUser = null;
        try {
            ObjectInputStream clientReader = new ObjectInputStream(clientSocket.socket().getInputStream());
            ObjectOutputStream clientWriter = new ObjectOutputStream(clientSocket.socket().getOutputStream());
            connectionManagerLogger.info("Открыты потоки ввода вывода");
            do {
                userRequest = (Request) clientReader.readObject();
                connectionManagerLogger.info("Получен запрос с командой " + userRequest.getCommandName(), userRequest);
                responseToUser = forkJoinPool.invoke(new RequestHandler(commandManager, userRequest));
                connectionManagerLogger.debug(forkJoinPool.toString());
                Response finalResponseToUser = responseToUser;
                cachedThreadPool.submit(() -> {
                    try {
                        connectionManagerLogger.debug(this.cachedThreadPool.toString());
                        clientWriter.writeObject(finalResponseToUser);
                        clientWriter.flush();
                        connectionManagerLogger.info("Ответ успешно отправлен");

                    } catch (IOException e) {
                        connectionManagerLogger.fatal("Не удалось отправить ответ");
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
                connectionManagerLogger.info("Клиент успешно отключен");
            } catch (IOException e) {
                connectionManagerLogger.error("Невозмоюно закрыть соединение ");
            }

        }
    }
}
