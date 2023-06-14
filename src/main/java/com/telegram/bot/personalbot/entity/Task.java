package com.telegram.bot.personalbot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "is_setting_deadline_date")
    private boolean isSettingDeadlineDate = false;

    @Column(name = "is_setting_deadline_time")
    private boolean isSettingDeadlineTime = false;

    @Column(name = "day_reminder_sent")
    private boolean dayReminderSent;

    @Column(name = "hour_reminder_sent")
    private boolean hourReminderSent;

    public Task(){}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String description;
        private LocalDateTime deadline;
        private Long chatId;
        private boolean isSettingDeadlineDate;
        private boolean isSettingDeadlineTime = false;
        private boolean dayReminderSent;
        private boolean hourReminderSent;

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder deadline(LocalDateTime deadline) {
            this.deadline = deadline;
            return this;
        }

        public Builder chatId(Long chatId) {
            this.chatId = chatId;
            return this;
        }

        public Builder isSettingDeadlineDate(boolean isSettingDeadlineDate){
            this.isSettingDeadlineDate = isSettingDeadlineDate;
            return this;
        }

        public Builder isSettingDeadlineTime(boolean isSettingDeadlineTime){
            this.isSettingDeadlineTime = isSettingDeadlineTime;
            return this;
        }

        public Builder dayReminderSent(boolean dayReminderSent) {
            this.dayReminderSent = dayReminderSent;
            return this;
        }

        public Builder hourReminderSent(boolean hourReminderSent) {
            this.hourReminderSent = hourReminderSent;
            return this;
        }

        public Task build() {
            Task task = new Task();
            task.description = this.description;
            task.deadline = this.deadline;
            task.chatId = this.chatId;
            task.isSettingDeadlineDate = this.isSettingDeadlineDate;
            task.isSettingDeadlineTime = this.isSettingDeadlineTime;
            task.dayReminderSent = this.dayReminderSent;
            task.hourReminderSent = this.hourReminderSent;
            return task;
        }
    }

    public boolean isSettingDeadlineTime() {
        return this.isSettingDeadlineTime;
    }
}