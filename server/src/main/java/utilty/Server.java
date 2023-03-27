package utilty;

import dtp.Request;
import dtp.Response;
import exceptions.ConnectionErrorException;
import exceptions.OpeningServerException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server {
    private int port;
    private int soTimeout;
    private Printable console;
    private ServerSocketChannel ss;
    private SocketChannel socketChannel;
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
                try (SocketChannel clientSocket = connectToClient()) {
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
            SocketAddress socketAddress = new InetSocketAddress(port);
            ss = ServerSocketChannel.open();
            ss.bind(socketAddress);
        } catch (IllegalArgumentException exception) {
            console.printError("Порт '" + port + "' находится за пределами возможных значений!");
            throw new OpeningServerException();
        } catch (IOException exception) {
            console.printError("Произошла ошибка при попытке использовать порт '" + port + "'!");
            throw new OpeningServerException();
        }
    }

    private SocketChannel connectToClient() throws ConnectionErrorException, SocketTimeoutException {
        try {
            console.println("Прослушивание порта '" + port + "'...");
            socketChannel = ss.accept();
            console.println("Соединение с клиентом успешно установлено.");
            return socketChannel;
        } catch (SocketTimeoutException exception) {
            console.printError("Превышено время ожидания подключения!");
            throw new SocketTimeoutException();
        } catch (IOException exception) {
            console.printError("Произошла ошибка при соединении с клиентом!");
            throw new ConnectionErrorException();
        }
    }

    private boolean processClientRequest(SocketChannel clientSocket) {
        Request userRequest = null;
        Response responseToUser = null;
        try {
            ObjectInputStream clientReader = new ObjectInputStream(clientSocket.socket().getInputStream());
            ObjectOutputStream clientWriter = new ObjectOutputStream(clientSocket.socket().getOutputStream());
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
            try{
                if (socketChannel == null) throw new ClosingSocketException();
                socketChannel.close();
                ss.close();
            } catch (ClosingSocketException exception) {
                console.printError("Невозможно завершить работу еще не запущенного сервера!");
            } catch (IOException exception) {
                    console.printError("Произошла ошибка при завершении работы сервера!");
            }
    }
}
