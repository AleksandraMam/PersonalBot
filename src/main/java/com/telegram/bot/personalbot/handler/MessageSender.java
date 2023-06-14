package com.telegram.bot.personalbot.handler;

import com.telegram.bot.personalbot.Config.BotConfig;
import com.telegram.bot.personalbot.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class MessageSender {

    private final BotConfig botConfig;

    @Autowired
    public MessageSender(@Lazy BotConfig botConfig) {
        this.botConfig = botConfig;
    }


    public void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            botConfig.myTelegramBot().execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageWithYesNoButtons(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Да");
        keyboard.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Нет");
        keyboard.add(row2);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(replyKeyboardMarkup);

        try {
            botConfig.myTelegramBot().execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public void sendTasksWithDeletionButtons(long chatId, List<Task> tasks) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберите задачу для удаления");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Task task : tasks) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(formatTask(task));
            button.setCallbackData("delete:" + task.getId());
            row.add(button);
            keyboard.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            botConfig.myTelegramBot().execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String formatTask(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getDescription());

        if (task.getDeadline() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            String formattedDeadline = task.getDeadline().format(formatter);
            sb.append(", дедлайн: ").append(formattedDeadline);
        }
        return sb.toString();
    }

    public void sendMainMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберите действие");

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Добавить задачу");
        keyboard.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Список всех задач");
        keyboard.add(row2);

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Удалить задачу");
        keyboard.add(row3);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(replyKeyboardMarkup);

        try {
            botConfig.myTelegramBot().execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

