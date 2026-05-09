package com.example.sport_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SportBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SportBeApplication.class, args);
    }

}
