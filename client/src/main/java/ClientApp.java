import java.util.Scanner;

import commandLine.*;
import commandLine.Console;
import exceptions.ConnectingException;
import exceptions.IllegalArguments;
import utilty.Client;
import utilty.RuntimeManager;

public class ClientApp {
    private static String host;
    private static int port;
    private static Printable console;

    public static boolean parseHostPort(String[] args){
        try{
            if(args.length != 2) throw new IllegalArguments("Передайте хост и порт в аргументы " +
                    "командной строки в формате <host> <port>");
            host = args[0];
            port = Integer.parseInt(args[1]);
            if(port < 0) throw new IllegalArguments("Порт должен быть натуральным числом");
            return true;
        } catch (IllegalArguments e) {
            console.printError(e.getMessage());
        }
        return false;
    }

    public static void main(String[] args) {
        if (!parseHostPort(args)) return;
        console = new Console();
        Client client = new Client(host, port, 5000, 5, console);
//        while(true){
//            try {
//                client.connectToServer();
//                break;
//            } catch (ConnectingException ex){
//                console.printError("Подключение к серверу не удалось");
//            }
//        }
        new RuntimeManager(console, new Scanner(System.in), client).interactiveMode();
    }
}
