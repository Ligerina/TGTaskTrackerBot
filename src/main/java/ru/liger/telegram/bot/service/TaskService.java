package ru.liger.telegram.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liger.telegram.bot.entity.TaskEntity;
import ru.liger.telegram.bot.repository.TaskRepository;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final Map<Long, TaskEntity> newTask = new HashMap<>();
    private final Map<Long, Boolean> waitingForDate = new HashMap<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public String startTaskCreation(Long chatId) {
        newTask.put(chatId, new TaskEntity());
        return "Введите название задачи";
    }

    public String getDateFromUser(Long chatId) {
        waitingForDate.put(chatId, true);
        return "Введите дату в формате дд.мм.гггг";
    }

    public String handleTaskStep(Long chatId, String message) {
        TaskEntity task = newTask.get(chatId);
        if (task.getTitle() == null) {
            task.setUserId(chatId);
            task.setTitle(message);
            return "Введите описание задачи, чтоб не облажаться в ее выполнении";
        } else if (task.getDescription() == null) {
            task.setDescription(message);
            return "Введите дату в формате дд.мм.гггг";
        } else {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                task.setData(simpleDateFormat.parse(message));
                saveTask(task);
                return "✅ Задача сохранена!\n" +
                        "📌 Название: " + task.getTitle() + "\n" +
                        "📝 Описание: " + task.getDescription() + "\n" +
                        "📅 Дата: " + message;
            } catch (ParseException e) {
                return "Кажется, вы проебались в дате, сээээр";
            }
        }
    }

    private void saveTask(TaskEntity task) {
        taskRepository.save(task);
    }

    public boolean isUserInTaskFlow(Long chatId) {
        return newTask.containsKey(chatId);
    }
    public boolean isWaitingForDate(Long userId) {
        return waitingForDate.getOrDefault(userId, false);
    }

    public String getAllTasks(Long userId) {
        Date today = new Date();
        List<TaskEntity> tasks = taskRepository.findByUserIdAndDataAfterOrderByDataAsc(userId, today);

        return stringBuilder(tasks, "📋 Ваши предстоящие задачи:\n");
    }

    public String getTaskByDate(Long userId, String findDate) {
        try {
            Date date = dateFormat.parse(findDate);
            List<TaskEntity> tasks = taskRepository.findByUserIdAndDataOrderByDataAsc(userId, date);
            return stringBuilder(tasks, "Ваши задачи на " + findDate);
        } catch (ParseException e) {
            return "Кажется, вы проебались в дате, сээээр";
        }
    }

    public String stringBuilder(List<TaskEntity> tasks, String header) {
        if (tasks.isEmpty()) {
            return "Задач нет, отдыхайте";
        }
        StringBuilder response = new StringBuilder(header);
        for (TaskEntity task : tasks) {
            response.append("\n✅ *")
                    .append(task.getTitle())
                    .append("*\n")
                    .append("📅 Дата: ")
                    .append(dateFormat.format(task.getData()))
                    .append("\n")
                    .append("📝 Описание: ")
                    .append(task.getDescription())
                    .append("\n");
        }
        return response.toString();
    }
}
