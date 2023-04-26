package managers;

import exceptions.InvalidForm;
import models.StudyGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.DatabaseHandler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Класс организующий работу с коллекцией
 */
public class CollectionManager {
    private final ArrayDeque<StudyGroup> collection = new ArrayDeque<>();
    /**
     * Дата создания коллекции
     */
    private LocalDateTime lastInitTime;
    /**
     * Дата последнего изменения коллекции
     */
    private LocalDateTime lastSaveTime;

    static final Logger collectionManagerLogger = LogManager.getLogger(CollectionManager.class);

    public CollectionManager() {
        this.lastInitTime = LocalDateTime.now();
        this.lastSaveTime = null;
        collection.addAll(DatabaseHandler.getDatabaseManager().loadCollection());
    }

    public synchronized ArrayDeque<StudyGroup> getCollection() {
        return collection;
    }
    /**
     * Метод скрывающий дату, если она сегодняшняя
     * @param localDateTime объект {@link LocalDateTime}
     * @return вывод даты
     */
    public static String timeFormatter(LocalDateTime localDateTime){
        if (localDateTime == null) return null;
        if (localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .equals(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))){
            return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Метод скрывающий дату, если она сегодняшняя
     * @param dateToConvert объект {@link Date}
     * @return вывод даты
     */
    public static String timeFormatter(Date dateToConvert){
        LocalDateTime localDateTime = dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        if (localDateTime == null) return null;
        if (localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .equals(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))){
            return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public synchronized String getLastInitTime() {
        return timeFormatter(lastInitTime);
    }

    public synchronized String getLastSaveTime() {
        return timeFormatter(lastSaveTime);
    }
    /**
     * @return Имя типа коллекции.
     */
    public synchronized String collectionType() {
        return collection.getClass().getName();
    }

    public synchronized int collectionSize() {
        return collection.size();
    }

    public synchronized void clear(){
        this.collection.clear();
        lastInitTime = LocalDateTime.now();
        collectionManagerLogger.info("Коллекция очищена");
    }

    public synchronized StudyGroup getLast() {
        return collection.getLast();
    }

    /**
     * @param id ID элемента.
     * @return Элемент по его ID или null, если не найдено.
     */
    public synchronized StudyGroup getById(int id) {
        for (StudyGroup element : collection) {
            if (element.getId() == id) return element;
        }
        return null;
    }

    /**
     * Изменить элемент коллекции с таким id
     * @param id id
     * @param newElement новый элемент
     * @throws InvalidForm Нет элемента с таким id
     */
    public synchronized void editById(int id, StudyGroup newElement){
        StudyGroup pastElement = this.getById(id);
        this.removeElement(pastElement);
        newElement.setId(id);
        this.addElement(newElement);
        collectionManagerLogger.info("Объект с айди " + id + " изменен", newElement);
    }

    /**
     * @param id ID элемента.
     * @return Проверяет, существует ли элемент с таким ID.
     */
    public synchronized boolean checkExist(int id) {
        return collection.stream()
                .anyMatch((x) -> x.getId() == id);
    }

    public synchronized void addElement(StudyGroup studyGroup){
        this.lastSaveTime = LocalDateTime.now();
        collection.add(studyGroup);
        collectionManagerLogger.info("Добавлен объект в коллекцию", studyGroup);
    }

    public synchronized void removeElement(StudyGroup studyGroup){
        collection.remove(studyGroup);
    }

    public synchronized void removeElements(Collection<StudyGroup> collection){this.collection.removeAll(collection);}

    public synchronized void removeElements(List<Integer> deletedIds){
        deletedIds
                .forEach((id) -> this.collection.remove(this.getById(id)));
    }

    @Override
    public synchronized String toString() {
        if (collection.isEmpty()) return "Коллекция пуста!";
        var last = getLast();

        StringBuilder info = new StringBuilder();
        for (StudyGroup studyGroup : collection) {
            info.append(studyGroup);
            if (studyGroup != last) info.append("\n\n");
        }
        return info.toString();
    }
}
