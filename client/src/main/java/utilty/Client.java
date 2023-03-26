import commandLine.ConsoleColors;
import commandLine.Printable;
import dtp.Request;
import dtp.Response;
import dtp.ResponseStatus;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Client {
    private String host;
    private int port;
    private int reconnectionTimeout;
    private int reconnectionAttempts;
    private int maxReconnectionAttempts;
    private Printable console;
    private SocketChannel socketChannel;
    private ObjectOutputStream serverWriter;
    private ObjectInputStream serverReader;

    public Client(String host, int port, int reconnectionTimeout, int maxReconnectionAttempts, Printable console) {
        this.host = host;
        this.port = port;
        this.reconnectionTimeout = reconnectionTimeout;
        this.maxReconnectionAttempts = maxReconnectionAttempts;
        this.console = console;
    }

    public Response sendAndAskResponse(Request request){
        while (true) {
            try {
                if (request.isEmpty()) return new Response(ResponseStatus.WRONG_ARGUMENTS, "Запрос пустой!");
                serverWriter.writeObject(request);
                return (Response) serverReader.readObject();
            } catch (IOException e) {
                console.printError("Соединение с сервером разорвано");
                try {
                    reconnectionAttempts++;
                    if (reconnectionAttempts >= maxReconnectionAttempts) {
                        console.printError("Превышено максимальное количество попыток соединения с сервером");
                        return new Response(ResponseStatus.EXIT);
                    }
                    Thread.sleep(reconnectionTimeout);
                    connectToServer();
                } catch (Exception ex) {
                    console.printError("Попытка соединения с сервером неуспешна");
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void connectToServer(){
        try{
            if(reconnectionAttempts > 0) console.println("Попытка повторного подключения", ConsoleColors.CYAN);
            socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
            console.println("Подключение успешно восстановлено", ConsoleColors.GREEN);
            this.serverReader = new ObjectInputStream(socketChannel.socket().getInputStream());
            this.serverWriter = new ObjectOutputStream(socketChannel.socket().getOutputStream());
            console.println("Обмен пакетами разрешен");
        } catch (IllegalArgumentException e){
            console.printError("Адрес сервера введен некорректно");
        } catch (IOException e) {
            console.printError("Произошла ошибка при соединении с сервером");
        }
    }
}
