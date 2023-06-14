package com.telegram.bot.personalbot.service;

import com.telegram.bot.personalbot.entity.Task;
import com.telegram.bot.personalbot.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(String text, LocalDateTime deadline, long chatId) {
        log.info("Creating task with text: {}", text);
        Task task = Task.builder()
                .description(text)
                .deadline(deadline)
                .chatId(chatId)
                .build();
        Task savedTask = taskRepository.save(task);
        log.info("Created task with id: {}", savedTask.getId());
        return savedTask;
    }

    public Task updateTask(Task task) {
        log.info("Updating task with id: {}", task.getId());
        Task updatedTask = taskRepository.save(task);
        log.info("Updated task with id: {}", updatedTask.getId());
        return updatedTask;
    }

    public List<Task> getTasksForUser(Long chatId) {
        return taskRepository.findByChatId(chatId);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }
}
