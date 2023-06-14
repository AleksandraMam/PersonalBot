package com.telegram.bot.personalbot.controller;

import com.telegram.bot.personalbot.entity.Task;
import com.telegram.bot.personalbot.handler.MessageSender;
import com.telegram.bot.personalbot.service.TaskService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;


@Component
@RequiredArgsConstructor
@Getter
@Setter
public class PersonalBot extends TelegramWebhookBot {
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.path}")
    private String botPath;

    private final TaskService taskService;
    private final Map<Long, Task> taskCreationMap;
    private final Map<Long, Boolean> taskDeletionMap;
    private final MessageSender messageSender;

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            System.out.println("Received message: " + messageText);

            processMessageCommand(chatId, messageText);
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            processCallbackCommand(chatId, callbackData);
        }

        return null;
    }

    private void processMessageCommand(long chatId, String messageText) {
        switch (messageText) {
            case "/start":
                messageSender.sendMessage(chatId, "Для начала работы с ботом нажмите на /menu");
                break;
            case "/menu":
                messageSender.sendMainMenu(chatId);
                break;
            case "Добавить задачу":
                handleAddTaskCommand(chatId);
                break;
            case "Список всех задач":
                handleListTasksCommand(chatId);
                break;
            case "Удалить задачу":
                handleDeleteTaskCommand(chatId);
                break;
            default:
                handleTaskCreation(chatId, messageText);
                break;
        }
    }

    private void handleListTasksCommand(long chatId) {
        List<Task> tasks = taskService.getTasksForUser(chatId);
        if (tasks.isEmpty()) {
            messageSender.sendMessage(chatId, "У вас нет задач");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Task task : tasks) {
                sb.append(messageSender.formatTask(task)).append("\n");
            }
            messageSender.sendMessage(chatId, sb.toString());
        }
    }

    private void processCallbackCommand(long chatId, String callbackData) {
        if (callbackData.startsWith("delete:")) {
            try {
                long taskId = Long.parseLong(callbackData.split(":")[1]);
                taskService.deleteTask(taskId);
                messageSender.sendMessage(chatId, "Задача удалена");
            } catch (NumberFormatException e) {
                messageSender.sendMessage(chatId, "Некорректный идентификатор задачи. Попробуйте ещё раз.");
            }
        }
    }

    private void handleAddTaskCommand(long chatId) {
        messageSender.sendMessage(chatId, "Введите текст задачи");
        taskCreationMap.put(chatId, Task.builder().chatId(chatId).build());
    }


    private void handleDeleteTaskCommand(long chatId) {
        List<Task> tasks = taskService.getTasksForUser(chatId);
        if (tasks.isEmpty()) {
            messageSender.sendMessage(chatId, "У вас нет задач");
        } else {
            messageSender.sendTasksWithDeletionButtons(chatId, tasks);
        }
    }

    private void handleTaskCreation(long chatId, String messageText) {
        if (taskCreationMap.containsKey(chatId)) {
            Task task = taskCreationMap.get(chatId);
            if (task.getDescription() == null) {
                task.setDescription(messageText);
                messageSender.sendMessageWithYesNoButtons(chatId, "Хотите ли вы установить дедлайн для этой задачи?");
            } else if (!task.isSettingDeadlineDate() && !task.isSettingDeadlineTime()) {
                if (messageText.equalsIgnoreCase("Да")) {
                    task.setSettingDeadlineDate(true);
                    messageSender.sendMessage(chatId, "Введите дату дедлайна в формате ГГГГ-ММ-ДД");
                } else if (messageText.equalsIgnoreCase("Нет")) {
                    taskService.createTask(task.getDescription(), task.getDeadline(), task.getChatId());
                    messageSender.sendMessage(chatId, "Задача успешно добавлена.\nЧтобы вернуться в главное меню нажмите /menu");
                    taskCreationMap.remove(chatId);
                }
            } else if (task.isSettingDeadlineDate() && !task.isSettingDeadlineTime()) {
                try {
                    LocalDate date = LocalDate.parse(messageText);
                    task.setDeadline(date.atStartOfDay());
                    task.setSettingDeadlineDate(false);
                    task.setSettingDeadlineTime(true);
                    messageSender.sendMessage(chatId, "Введите время дедлайна в формате ЧЧ:ММ");
                } catch (DateTimeParseException e) {
                    messageSender.sendMessage(chatId, "Неверный формат даты. Пожалуйста, попробуйте ещё раз.");
                }
            } else if (!task.isSettingDeadlineDate() && task.isSettingDeadlineTime()) {
                try {
                    LocalTime time = LocalTime.parse(messageText);
                    task.setDeadline(task.getDeadline().with(time));
                    task.setSettingDeadlineTime(false);
                    taskService.createTask(task.getDescription(), task.getDeadline(), task.getChatId());
                    messageSender.sendMessage(chatId, "Задача успешно добавлена");
                    taskCreationMap.remove(chatId);
                } catch (DateTimeParseException e) {
                    messageSender.sendMessage(chatId, "Неверный формат времени. Пожалуйста, попробуйте ещё раз.");
                }
            }
        }
    }


    @Override
    public String getBotPath() {
        return botPath;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}


