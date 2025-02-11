package ru.liger.telegram.bot.controller;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liger.telegram.bot.service.TaskService;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;
    private final TaskService taskService;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                sendMenuKeyboard(chatId, "Привет! Выберите команду:");
            } else if (messageText.equals("/addTask")) {
                sendMessage(chatId, taskService.startTaskCreation(chatId));
            } else if (messageText.equals("/tasks")) {
                sendMessage(chatId, taskService.getAllTasks(chatId));
            } else if (messageText.equals("/taskOnDate")) {
                sendMessage(chatId, taskService.getDateFromUser(chatId));
            } else if (taskService.isWaitingForDate(chatId)) {
                sendMessage(chatId, taskService.getTaskByDate(chatId, messageText));
            } else if (taskService.isUserInTaskFlow(chatId)) {
                sendMessage(chatId, taskService.handleTaskStep(chatId, messageText));
            } else {
                sendMessage(chatId, "Неизвестная команда. Одна ошибка и вы ошиблись!");
            }
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMenuKeyboard(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(getKeyboard());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup getKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("/addTask");
        row1.add("/tasks");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("/taskOnDate");

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
