package com.telegram.bot.personalbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RestController
public class WebHookController {

    private final PersonalBot personalBot;

    @Autowired
    public WebHookController(PersonalBot personalBot) {
        this.personalBot = personalBot;
    }

    @PostMapping("/")
    public void receiveUpdate(@RequestBody Update update) {
        personalBot.onWebhookUpdateReceived(update);
    }

}