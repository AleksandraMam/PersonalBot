package com.telegram.bot.personalbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PersonalBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalBotApplication.class, args);
    }

}
