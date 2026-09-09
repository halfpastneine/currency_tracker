package com.example.currencywatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class CurrencyWatcher {
    public static void main(String[] args) {
        System.out.println("Starting Currency Watcher...");
        SpringApplication.run(CurrencyWatcher.class, args);
        System.out.println("Currency Watcher is ready at http://localhost:8080");
    }
}