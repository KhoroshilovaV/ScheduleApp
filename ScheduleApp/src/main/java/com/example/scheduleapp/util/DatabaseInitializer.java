package com.example.scheduleapp.util;

import com.example.scheduleapp.config.DatabaseCreator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;

@ComponentScan(
        basePackages = "com.example.scheduleapp",
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = DatabaseCreator.class),
        useDefaultFilters = false
)
@PropertySource("classpath:application.properties")
public class DatabaseInitializer {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DatabaseInitializer.class)) {
            DatabaseCreator databaseCreator = context.getBean(DatabaseCreator.class);
            databaseCreator.createDatabaseAndTables();
            System.out.println("Database and tables initialized successfully!");
        } catch (Exception e) {
            System.err.println("Error during database initialization: " + e.getMessage());
        }
    }
}