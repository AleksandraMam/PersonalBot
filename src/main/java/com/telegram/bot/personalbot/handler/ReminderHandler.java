package com.telegram.bot.personalbot.handler;

import com.telegram.bot.personalbot.entity.Task;
import com.telegram.bot.personalbot.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderHandler {

    private final TaskService taskService;
    private final MessageSender messageSender;

    @Scheduled(cron = "0 * * * * *") // Запуск каждую минуту
    public void sendReminders() {
        List<Task> tasks = taskService.getAllTasks();
        LocalDateTime now = LocalDateTime.now();
        for (Task task : tasks) {
            LocalDateTime deadline = task.getDeadline();
            if (deadline != null) {
                Duration durationToDeadline = Duration.between(now, deadline);
                long hoursToDeadline = durationToDeadline.toHours();
                long minutesToDeadline = durationToDeadline.toMinutes();

                // За один день до дедлайна
                if (hoursToDeadline == 24 && !task.isDayReminderSent()) {
                    messageSender.sendMessage(task.getChatId(), "У вас остался 1 день на выполнение задачи: " + task.getDescription());
                    task.setDayReminderSent(true);
                    taskService.updateTask(task);
                }
                // За один час до дедлайна
                else if (minutesToDeadline <= 60 && minutesToDeadline > 59 && !task.isHourReminderSent()) {
                    messageSender.sendMessage(task.getChatId(), "У вас остался 1 час на выполнение задачи: " + task.getDescription());
                    task.setHourReminderSent(true);
                    taskService.updateTask(task);
                }
            }
        }
    }
}
