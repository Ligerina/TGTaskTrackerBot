package ru.liger.telegram.bot.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.liger.telegram.bot.controller.TelegramBot;
import ru.liger.telegram.bot.service.TaskService;



@Configuration
public class TelegramBotConfig {
    @Value("${telegram.bot.name}")
    private String name;
    @Value("${telegram.bot.token}")
    private String token;
    private final TaskService taskService;

    public TelegramBotConfig(TaskService taskService) {
        this.taskService = taskService;
    }

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(name, token, taskService);
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBot bot) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
        return telegramBotsApi;
    }
}
