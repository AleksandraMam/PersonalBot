package com.telegram.bot.personalbot.Config;

import com.telegram.bot.personalbot.controller.PersonalBot;
import com.telegram.bot.personalbot.entity.Task;
import com.telegram.bot.personalbot.handler.MessageSender;
import com.telegram.bot.personalbot.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class BotConfig {
    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.path}")
    private String botPath;

    private final TaskService taskService;

    private final Map<Long, Task> taskCreationMap;
    private final Map<Long, Boolean> taskDeletionMap;
    private final MessageSender messageSender;


    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory httpRequestFactory = new SimpleClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(30000);
        httpRequestFactory.setReadTimeout(30000);
        return new RestTemplate(httpRequestFactory);
    }


    @Bean
    public PersonalBot myTelegramBot() {
        setWebHook(restTemplate());
        PersonalBot myTelegramBot = new PersonalBot(taskService, taskCreationMap, taskDeletionMap, messageSender);
        myTelegramBot.setBotPath(botPath);
        myTelegramBot.setBotName(botName);
        myTelegramBot.setBotToken(botToken);
        return myTelegramBot;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    public void setWebHook(RestTemplate restTemplate) {
        String url = "https://api.telegram.org/bot" + botToken + "/setWebhook?url=" + botPath;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
        System.out.println("Webhook response: " + response.getBody());
    }
}