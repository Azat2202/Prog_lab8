package managers;

import commands.CollectionEditor;
import commands.Command;
import dtp.Request;
import dtp.Response;
import exceptions.CommandRuntimeError;
import exceptions.ExitObliged;
import exceptions.IllegalArguments;
import exceptions.NoSuchCommand;
import utilty.Console;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Командный менеджер.
 * Реализует паттерн программирования Command
 */
public class CommandManager{
    /**
     * Поле для хранения комманд в виде Имя-Комманда
     */
    private final HashMap<String, Command> commands = new HashMap<>();
    /**
     * Поле для истории команд
     */
    private final List<String> commandHistory = new ArrayList<>();
    private final FileManager fileManager;

    public CommandManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void addCommand(Command command){
        this.commands.put(command.getName(), command);
    }
    public void addCommand(Collection<Command> commands){
        this.commands.putAll(commands.stream()
                .collect(Collectors.toMap(Command::getName, s -> s)));
    }
    public Collection<Command> getCommands(){
        return commands.values();
    }

    public void addToHistory(String line){
        if(line.isBlank()) return;
        this.commandHistory.add(line);
    }

    public List<String> getCommandHistory(){return commandHistory;}

    /**
     * Выполняет команду
     * @param request - запрос клиента
     * @throws NoSuchCommand такая команда отсутствует
     * @throws IllegalArguments неверные аргументы команды
     * @throws CommandRuntimeError команда выдала ошибку при исполнении
     * @throws ExitObliged команда вызвала выход из программы
     */
    public Response execute(Request request) throws NoSuchCommand, IllegalArguments, CommandRuntimeError, ExitObliged {
        Command command = commands.get(request.getCommandName());
        if (command == null) throw new NoSuchCommand();
        Response response = command.execute(request);
        if (command instanceof CollectionEditor) fileManager.saveObjects();
        return response;
    }
}
