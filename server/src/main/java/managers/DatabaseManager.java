package managers;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import main.App;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseManager {
    private Connection connection;

    static final Logger databaseLogger = LogManager.getLogger(DatabaseManager.class);

    public DatabaseManager(){
        try {
            this.connect();
            this.createMainBase();
        } catch (SQLException e) {
            databaseLogger.fatal("Ошибка при исполнени изначального запроса");
            databaseLogger.fatal(e.toString());
        }
    }

    public void connect(){
        Properties info = null;
        try {
            info = new Properties();
            info.load(new FileInputStream(App.DATABASE_CONFIG_PATH));
            connection = DriverManager.getConnection(App.DATABASE_URL, info);
            databaseLogger.info("Успешно подключен к базе данных");
            databaseLogger.debug(connection);
        } catch (SQLException | IOException e) {
            try{
                connection = DriverManager.getConnection(App.DATABASE_URL_HELIOS, info);
                databaseLogger.info("Успешно подключен к базе данных");
            } catch (SQLException ex) {
                databaseLogger.fatal("Невозможно подключиться к базе данных");
            }
        }
    }

    public void createMainBase() throws SQLException {
        connection
                .prepareStatement(DatabaseCommands.allTablesCreation)
                .execute();
        databaseLogger.info("Таблицы созданы");
    }
}
