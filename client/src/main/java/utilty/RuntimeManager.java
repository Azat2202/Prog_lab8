import exceptions.CommandRuntimeError;
import exceptions.ExitObliged;
import exceptions.IllegalArguments;
import exceptions.NoSuchCommand;
import commandLine.*;

import java.util.*;

/**
 * Класс обработки пользовательского ввода
 * @author azat2202
 */
public class RuntimeManager {
    private final Printable console;
    private final Scanner userScanner;

    public RuntimeManager(Printable console, Scanner userScanner) {
        this.console = console;
        this.userScanner = userScanner;
    }

    /**
     * Перманентная работа с пользователем и выполнение команд
     */
    public void interactiveMode(){
        while (true) {
            try{
                if (!userScanner.hasNext()) throw new ExitObliged();
                String userCommand = userScanner.nextLine().trim() + " "; // прибавляем пробел, чтобы split выдал два элемента в массиве
            } catch (NoSuchElementException exception) {
                console.printError("Пользовательский ввод не обнаружен!");
            } catch (ExitObliged exitObliged){
                console.println(ConsoleColors.toColor("До свидания!", ConsoleColors.YELLOW));
                return;
            }
        }
    }
}
