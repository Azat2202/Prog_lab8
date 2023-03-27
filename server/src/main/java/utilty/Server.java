package utilty;

import dtp.Request;
import dtp.Response;
import dtp.ResponseStatus;
import exceptions.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class Server {
    private int port;
    private int soTimeout;
    private Printable console;
    private ServerSocket serverSocket;
    private RequestHandler requestHandler;

    public Server(int port, int soTimeout, RequestHandler handler) {
        this.port = port;
        this.soTimeout = soTimeout;
        this.console = new Console();
        this.requestHandler = handler;
    }

    public void run(){
        try{
            openServerSocket();
            while (true) {
                try (Socket clientSocket = connectToClient()) {
                    if(!processClientRequest(clientSocket)) break;
                } catch (ConnectionErrorException | SocketTimeoutException exception) {
                    break;
                } catch (IOException exception) {
                    console.printError("Произошла ошибка при попытке завершить соединение с клиентом!");
                }
            }
            stop();
        } catch (OpeningServerException e) {
            console.printError("Сервер не может быть запущен");
        }
    }

    private void openServerSocket() throws OpeningServerException{
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(soTimeout);
        } catch (IllegalArgumentException exception) {
            console.printError("Порт '" + port + "' находится за пределами возможных значений!");
            throw new OpeningServerException();
        } catch (IOException exception) {
            console.printError("Произошла ошибка при попытке использовать порт '" + port + "'!");
            throw new OpeningServerException();
        }
    }

    private Socket connectToClient() throws ConnectionErrorException, SocketTimeoutException {
        try {
            console.println("Прослушивание порта '" + port + "'...");
            Socket clientSocket = serverSocket.accept();
            console.println("Соединение с клиентом успешно установлено.");
            return clientSocket;
        } catch (SocketTimeoutException exception) {
            console.printError("Превышено время ожидания подключения!");
            throw new SocketTimeoutException();
        } catch (IOException exception) {
            console.printError("Произошла ошибка при соединении с клиентом!");
            throw new ConnectionErrorException();
        }
    }

    private boolean processClientRequest(Socket clientSocket) {
        Request userRequest = null;
        Response responseToUser = null;
        try {
            ObjectInputStream clientReader = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream clientWriter = new ObjectOutputStream(clientSocket.getOutputStream());
            do {
                userRequest = (Request) clientReader.readObject();
                console.println(userRequest.toString());
                responseToUser = requestHandler.handle(userRequest);
                clientWriter.writeObject(responseToUser);
                clientWriter.flush();
            } while (true);
        } catch (ClassNotFoundException exception) {
            console.printError("Произошла ошибка при чтении полученных данных!");
        } catch (InvalidClassException | NotSerializableException exception) {
            console.printError("Произошла ошибка при отправке данных на клиент!");
        } catch (IOException exception) {
            if (userRequest == null) {
                console.printError("Непредвиденный разрыв соединения с клиентом!");
            } else {
                console.println("Клиент успешно отключен от сервера!");
            }
        }
        return true;
    }

    private void stop() {
        class ClosingSocketException extends Exception{}
        try {
            if (serverSocket == null) throw new ClosingSocketException();
            serverSocket.close();
            console.println("Работа сервера успешно завершена.");
        } catch (ClosingSocketException exception) {
            console.printError("Невозможно завершить работу еще не запущенного сервера!");
        } catch (IOException exception) {
            console.printError("Произошла ошибка при завершении работы сервера!");
        }
    }
}
