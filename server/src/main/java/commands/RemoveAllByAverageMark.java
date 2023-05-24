package commands;

import dtp.Request;
import dtp.Response;
import dtp.ResponseStatus;
import exceptions.IllegalArguments;
import managers.CollectionManager;
import models.StudyGroup;
import utility.DatabaseHandler;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Команда 'remove_all_by_average_mark'
 * Удаляет из коллекции все элементы, значение поля average_mark которого эквивалентно заданному
 */
public class RemoveAllByAverageMark extends Command implements CollectionEditor{
    private final CollectionManager collectionManager;

    public RemoveAllByAverageMark(CollectionManager collectionManager) {
        super("remove_all_by_average_mark", "  average_mark : удалить из коллекции все элементы, значение поля average_mark которого эквивалентно заданному");
        this.collectionManager = collectionManager;
    }

    /**
     * Исполнить команду
     * @param request аргументы команды
     * @throws IllegalArguments неверные аргументы команды
     */
    @Override
    public Response execute(Request request) throws IllegalArguments {
        if (request.getArgs().isBlank()) throw new IllegalArguments();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Response", request.getLocale());
        try {
            long averageMark = Long.parseLong(request.getArgs().trim());
            List<StudyGroup> toRemove = collectionManager.getCollection().stream()
                    .filter(Objects::nonNull)
                    .filter(studyGroup -> studyGroup.getAverageMark() == averageMark)
                    .filter(studyGroup -> studyGroup.getUserLogin().equals(request.getUser().name()))
                    .filter((obj) -> DatabaseHandler.getDatabaseManager().deleteObject(obj.getId(), request.getUser()))
                    .toList();
            collectionManager.removeElements(toRemove);
            return new Response(ResponseStatus.OK, resourceBundle.getString("avgMarkDeleted"));
        } catch (NumberFormatException exception) {
            return new Response(ResponseStatus.ERROR,resourceBundle.getString("avgMarkLong"));
        }
    }
}
