package com.example.scheduleapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Component
public class DatabaseCreator {

    @Value("${spring.datasource.url}")
    private String datasourceUrl; // Полный URL для подключения

    @Value("${spring.datasource.username}")
    private String username; // Имя пользователя PostgreSQL

    @Value("${spring.datasource.password}")
    private String password; // Пароль пользователя PostgreSQL

    @Value("${app.database.name:schedule}") // Имя создаваемой базы данных
    private String databaseName;

    public void createDatabaseAndTables() {
        // Принудительно заменяем URL базы данных на "postgres"
        String baseUrl = datasourceUrl.substring(0, datasourceUrl.lastIndexOf("/") + 1) + "postgres";

        try (Connection connection = DriverManager.getConnection(baseUrl, username, password);
             Statement statement = connection.createStatement()) {

            // Проверяем, существует ли база данных
            String checkDatabaseExists = "SELECT 1 FROM pg_database WHERE datname = '" + databaseName + "'";
            var resultSet = statement.executeQuery(checkDatabaseExists);

            if (!resultSet.next()) {
                // Создаем базу данных, если она отсутствует
                statement.executeUpdate("CREATE DATABASE " + databaseName);
                System.out.println("Database '" + databaseName + "' created successfully!");
            } else {
                System.out.println("Database '" + databaseName + "' already exists.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create database '" + databaseName + "'", e);
        }

        // Подключаемся к созданной базе данных и создаем таблицы
        createTablesInDatabase();
    }

    private void createTablesInDatabase() {
        String databaseUrl = datasourceUrl; // Полный URL для подключения к целевой базе данных

        try (Connection connection = DriverManager.getConnection(databaseUrl, username, password);
             Statement statement = connection.createStatement()) {

            // Создание таблиц
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS days (
                    id SERIAL PRIMARY KEY,
                    day_name VARCHAR(50) UNIQUE NOT NULL,
                    "order" INT NOT NULL
                );
            """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS records (
                    id SERIAL PRIMARY KEY,
                    date DATE NOT NULL,
                    start_time TIME NOT NULL,
                    end_time TIME NOT NULL,
                    subject VARCHAR(255) NOT NULL,
                    mode VARCHAR(50) NOT NULL
                );
            """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS day_record_links (
                    id SERIAL PRIMARY KEY,
                    day_id INT NOT NULL,
                    record_id INT NOT NULL,
                    FOREIGN KEY (day_id) REFERENCES days (id) ON DELETE CASCADE,
                    FOREIGN KEY (record_id) REFERENCES records (id) ON DELETE CASCADE
                );
            """);

            System.out.println("Tables 'days', 'records', and 'day_record_links' created successfully!");

            // Заполнение таблицы days русскими днями недели с указанием порядка
            statement.executeUpdate("""
                INSERT INTO days (day_name, "order")
                VALUES
                    ('Понедельник', 1),
                    ('Вторник', 2),
                    ('Среда', 3),
                    ('Четверг', 4),
                    ('Пятница', 5),
                    ('Суббота', 6),
                    ('Воскресенье', 7)
                ON CONFLICT (day_name) DO NOTHING;  -- Если дни недели уже есть в таблице, не вставлять их повторно
            """);

            System.out.println("Days inserted into table 'days' successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize tables in database '" + databaseName + "'", e);
        }
    }
}