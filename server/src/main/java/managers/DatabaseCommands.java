package managers;

public class DatabaseCommands {
    public static String allTablesCreation = """
            DROP TYPE IF EXISTS FORM_OF_EDUCATION;
            DROP TYPE IF EXISTS COLOR;
            DROP TYPE IF EXISTS COUNTRY;
            CREATE TYPE FORM_OF_EDUCATION AS ENUM(
                'DISTANCE_EDUCATION',
                'FULL_TIME_EDUCATION',
                'EVENING_CLASSES'
            );
            CREATE TYPE COLOR AS ENUM (
                'GREEN',
                'RED',
                'ORANGE',
                'WHITE',
                'BROWN'
            );
            CREATE TYPE COUNTRY AS ENUM(
                'UNITED_KINGDOM',
                'FRANCE',
                'CHINA',
                'INDIA',
                'SOUTH_KOREA'
            );
            CREATE TABLE IF NOT EXISTS studyGroup (
                id SERIAL PRIMARY KEY,
                group_name TEXT NOT NULL ,
                cord_x NUMERIC NOT NULL,
                cord_y NUMERIC NOT NULL ,
                creation_date DATE NOT NULL ,
                students_count BIGINT NOT NULL ,
                expelled_students BIGINT NOT NULL ,
                average_mark BIGINT NOT NULL ,
                form_of_education FORM_OF_EDUCATION,
                person_name TEXT NOT NULL ,
                person_weight INT NOT NULL ,
                person_eye_color COLOR,
                person_hair_color COLOR,
                person_nationality COUNTRY,
                person_location_x BIGINT NOT NULL,
                person_location_y BIGINT NOT NULL,
                person_location_name TEXT NOT NULL
            );
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                login TEXT,
                password TEXT
            );
            """;

}
