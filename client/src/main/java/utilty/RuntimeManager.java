package utilty;

import commandLine.forms.StudyGroupForm;
import dtp.Request;
import dtp.Response;
import dtp.ResponseStatus;
import exceptions.CommandRuntimeError;
import exceptions.ExitObliged;
import exceptions.IllegalArguments;
import exceptions.NoSuchCommand;
import commandLine.*;
import models.StudyGroup;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Класс обработки пользовательского ввода
 * @author azat2202
 */
public class RuntimeManager {
    private final Printable console;
    private final Scanner userScanner;
    private final Client client;

    public RuntimeManager(Printable console, Scanner userScanner, Client client) {
        this.console = console;
        this.userScanner = userScanner;
        this.client = client;
    }

    /**
     * Перманентная работа с пользователем и выполнение команд
     */
    public void interactiveMode(){
        while (true) {
            try{
                if (!userScanner.hasNext()) throw new ExitObliged();
                String[] userCommand = (userScanner.nextLine().trim() + " ").split(" ", 2); // прибавляем пробел, чтобы split выдал два элемента в массиве
                Response response = client.sendAndAskResponse(new Request(userCommand[0].trim(), userCommand[1].trim()));
                this.printResponse(response);
                switch (response.getStatus()){
                    case OK, WRONG_ARGUMENTS, ERROR -> {}
                    case ASK_OBJECT -> {
                        StudyGroup studyGroup = new StudyGroupForm(console).build();
                        Response newResponse = client.sendAndAskResponse(
                                new Request(
                                userCommand[0].trim(),
                                userCommand[1].trim(),
                                studyGroup));
                        if (newResponse.getStatus() != ResponseStatus.OK){
                            console.printError(newResponse.getResponse());
                        }
                        else {
                            this.printResponse(newResponse);
                        }
                    }
                    case EXIT -> throw new ExitObliged();
                }
            } catch (NoSuchElementException exception) {
                console.printError("Пользовательский ввод не обнаружен!");
            } catch (ExitObliged exitObliged){
                console.println(ConsoleColors.toColor("До свидания!", ConsoleColors.YELLOW));
                return;
            }
        }
    }

    private void printResponse(Response response){
        switch (response.getStatus()){
            case OK -> {
                if ((Objects.isNull(response.getCollection()))) {
                    console.println(response.getResponse(), ConsoleColors.GREEN);
                } else {
                    console.println(response.getResponse() + "\n" + response.getCollection().toString());
                }
            }
            case ERROR -> console.printError(response.getResponse());
            case WRONG_ARGUMENTS -> console.printError("Неверное использование команды!");
            case EXIT, ASK_OBJECT -> {}
        }
    }
}
