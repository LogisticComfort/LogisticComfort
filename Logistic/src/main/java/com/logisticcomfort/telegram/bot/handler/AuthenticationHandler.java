package com.logisticcomfort.telegram.bot.handler;

import com.logisticcomfort.model.COMMANDS;
import com.logisticcomfort.model.HistoryMessage;
import com.logisticcomfort.model.TelegramUser;
import com.logisticcomfort.repos.HistoryMessageRepo;
import com.logisticcomfort.service.TelegramService;
import com.logisticcomfort.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class AuthenticationHandler {
    private static TelegramService telegramService;
    private static UserService userService;

    @Autowired
    public AuthenticationHandler(TelegramService telegramService, UserService userService) {
        this.telegramService = telegramService;
        this.userService = userService;
    }

    public static SendMessage authentication(Long chatId){
        telegramService.saveTelegramUser(new TelegramUser(chatId));
        var sendMeassge = new SendMessage(String.valueOf(chatId), "");
        Buttons.setAuthenticationButtons(sendMeassge);
        return sendMeassge;
    }

    public static SendMessage handleWriteLogin(Long chatId, String text){
        var telegramUser = telegramService.findUserByChatId(chatId);
        if (telegramUser.getMessage() == null){
            var lastMessage = new HistoryMessage(telegramUser, COMMANDS.LOG_IN_ACCOUNT.getCommand());
            telegramUser.setMessage(lastMessage);
            telegramService.saveHistoryMessage(lastMessage);
            telegramService.saveTelegramUser(telegramUser);
            return new SendMessage(String.valueOf(chatId), "Введите свой логин: ");
        }
        if(text.equals(COMMANDS.START.getCommand())){
            var message = telegramUser.getMessage();
            message.setMessage(COMMANDS.LOG_IN_ACCOUNT.getCommand());
            telegramService.saveHistoryMessage(message);
            return new SendMessage(String.valueOf(chatId), "Введите свой логин: ");
        }
        if(telegramUser.getMessage().getMessage().equals(COMMANDS.LOG_IN_ACCOUNT.getCommand())) {
            var message = telegramService.findMessageByTelegramUser(telegramUser);
            message.setMessage("login: " + text);
            telegramService.saveHistoryMessage(message);
            return new SendMessage(String.valueOf(chatId), String.format("Введите свой пароль от аккаунта %s: ", text));
        }
        if(telegramUser.getMessage().getMessage().substring(0,6).equals("login:")){
            var username = telegramUser.getMessage().getMessage().replace("login:", "");
            username = username.replace(" ", "");
            var user = userService.findUserByUsername(username);
            if (user == null){
                telegramService.deleteHistoryMessageById(telegramUser.getMessage().getId());
                var lastMessage = new HistoryMessage(telegramUser, COMMANDS.LOG_IN_ACCOUNT.getCommand());
                telegramUser.setMessage(lastMessage);
                telegramService.saveHistoryMessage(lastMessage);
                telegramService.saveTelegramUser(telegramUser);
                return new SendMessage(String.valueOf(chatId), "Введите свой логин: ");
            }
            if (user.getPassword() == text){
                return new SendMessage(String.valueOf(chatId), String.format("Введите свой пароль от аккаунта %s: ", text));
            }

            telegramUser.setUser(user);
            telegramService.saveTelegramUser(telegramUser);
            var message = telegramUser.getMessage();
            message.setMessage("");
            telegramService.saveHistoryMessage(message);
            return new SendMessage(String.valueOf(chatId), String.format("%s, вы успешно авторизовались!", user.getFullName()));
        }
        return new SendMessage(String.valueOf(chatId), "Такой команды нет");
    }
}
