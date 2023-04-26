package commands;

import dtp.Request;
import dtp.Response;
import dtp.ResponseStatus;
import dtp.User;
import exceptions.IllegalArguments;
import managers.CollectionManager;
import utility.DatabaseHandler;

/**
 * Команда 'clear'
 * Очищает коллекцию
 */
public class Clear extends Command implements CollectionEditor{
    private CollectionManager collectionManager;

    public Clear(CollectionManager collectionManager) {
        super("clear", ": очистить коллекцию");
        this.collectionManager = collectionManager;
    }

    /**
     * Исполнить команду
     * @param request аргументы команды
     * @throws IllegalArguments неверные аргументы команды
     */
    @Override
    public Response execute(Request request) throws IllegalArguments {
        if (!request.getArgs().isBlank()) throw new IllegalArguments();
        if(DatabaseHandler.getDatabaseManager().deleteAllObjects(request.getUser())) {
            collectionManager.reloadFromDatabase();
            return new Response(ResponseStatus.OK, "Ваши элементы удалены");
        }
        return new Response(ResponseStatus.ERROR, "Элементы коллекции удалить не удалось");
    }
}
