package managers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.security.AnyTypePermission;
import exceptions.ExitObliged;
import exceptions.InvalidForm;
import models.StudyGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.ConsoleColors;
import utility.Printable;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Класс реализующий работу с файлами
 * @author azat2202
 */
public class FileManager {
    private String text;
    private final CollectionManager collectionManager;
    private final XStream xStream = new XStream();

    static final Logger fileManagerLogger = LogManager.getLogger(FileManager.class);


    /**
     * В конструкторе задаются алиасы для библиотеки {@link XStream} которая используется для работы с xml
     * @param console Пользовательский ввод-вывод
     * @param collectionManager Работа с коллекцией
     */
    public FileManager(Printable console, CollectionManager collectionManager) {
        this.collectionManager = collectionManager;

        this.xStream.alias("StudyGroup", StudyGroup.class);
        this.xStream.alias("Array", CollectionManager.class);
        this.xStream.addPermission(AnyTypePermission.ANY);
        this.xStream.addImplicitCollection(CollectionManager.class, "collection");
        fileManagerLogger.info("Созданы алиасы для xstream");
    }

    /**
     * Обращение к переменным среды и чтение файла в поле по указанному пути
     * @throws ExitObliged если путь - null или отсутствует программа заканчивает выполнение
     */
    public void findFile() throws ExitObliged{
        String file_path = System.getenv("file_path");
        if (file_path == null || file_path.isEmpty()) {
            fileManagerLogger.fatal("Нет пути в переменных окружения");
            throw new ExitObliged();
        }
        File file = new File(file_path);
        BufferedInputStream bis;
        FileInputStream fis;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            while (bis.available() > 0) {
                stringBuilder.append((char) bis.read());
            }
            fis.close();
            bis.close();
            if (stringBuilder.isEmpty()) {
                fileManagerLogger.info("Файл пустой");
                this.text = "</Array>";
                return;
            }
            this.text = stringBuilder.toString();
        } catch (FileNotFoundException fnfe) {
            fileManagerLogger.fatal("Такого файла не найдено");
            throw new ExitObliged();
        } catch (IOException ioe) {
            fileManagerLogger.fatal("Ошибка ввода/вывода" + ioe);
            throw new ExitObliged();
        }
    }

    /**
     * Создание объектов в консольном менеджере
     * @throws ExitObliged Если объекты в файле невалидны выходим из программы
     */
    public void createObjects() throws ExitObliged{
        try{
            XStream xstream = new XStream();
            xstream.alias("StudyGroup", StudyGroup.class);
            xstream.alias("Array", CollectionManager.class);
            xstream.addPermission(AnyTypePermission.ANY);
            xstream.addImplicitCollection(CollectionManager.class, "collection");
            CollectionManager collectionManagerWithObjects = (CollectionManager) xstream.fromXML(this.text);
            for(StudyGroup s : collectionManagerWithObjects.getCollection()){
                if (this.collectionManager.checkExist(s.getId())){
                    throw new ExitObliged();
                }
                if (!s.validate()) throw new InvalidForm();
                this.collectionManager.addElement(s);
            }
        } catch (InvalidForm invalidForm) {
            fileManagerLogger.fatal("Объекты в файле не валидны");
            throw new ExitObliged();
        } catch (StreamException streamException){
            fileManagerLogger.error("Файл пустой");
        }
        CollectionManager.updateId(collectionManager.getCollection());
    }

    /**
     * Сохраняем коллекцию из менеджера в файл
     */
    public void saveObjects(){
        String file_path = System.getenv("file_path");
        if (file_path == null || file_path.isEmpty()) {
            fileManagerLogger.fatal("Отсутствует путь в переменных окружения");
        }
        else {
            fileManagerLogger.info(ConsoleColors.toColor("Путь получен успешно", ConsoleColors.PURPLE));
        }

        try{
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file_path));
            out.write(this.xStream.toXML(collectionManager)
                    .getBytes(StandardCharsets.UTF_8));
            out.close();
            fileManagerLogger.info("Файл записан");
        } catch (FileNotFoundException e) {
            fileManagerLogger.error("Файл не существует");
        }catch (IOException e){
            fileManagerLogger.error("Ошибка ввода вывода");
        }
    }
}