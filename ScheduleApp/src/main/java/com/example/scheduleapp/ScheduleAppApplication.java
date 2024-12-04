package com.example.scheduleapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages = "com.example.scheduleapp")
public class ScheduleAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScheduleAppApplication.class, args);
    }
}