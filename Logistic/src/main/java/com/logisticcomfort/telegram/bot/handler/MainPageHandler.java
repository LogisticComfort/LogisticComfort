package com.logisticcomfort.telegram.bot.handler;

import com.logisticcomfort.model.COMMANDS;
import com.logisticcomfort.service.TelegramService;
import com.logisticcomfort.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Component
public class MainPageHandler {
    private static TelegramService telegramService;
    private static UserService userService;

    @Autowired
    public MainPageHandler(TelegramService telegramService, UserService userService) {
        this.telegramService = telegramService;
        this.userService = userService;
    }

    public static SendMessage mainPage(Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setText("Your menu");
        var buttonMainPage = new KeyboardButton();
        buttonMainPage.setText(COMMANDS.MAIN_PAGE.getCommand());
        var firstKeyboardRow = new KeyboardRow();
        firstKeyboardRow.add(buttonMainPage);

        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(List.of(
                firstKeyboardRow
        ));
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }

    public static SendMessage showMenu(Long chatId, String text){
        var user = telegramService.findUserByChatId(chatId).getUser();

        var telegramUser = telegramService.findUserByChatId(chatId);
        var historyMessage = telegramService.findMessageByTelegramUser(telegramUser);

        historyMessage.setMessage("");
        telegramService.saveHistoryMessage(historyMessage);

        switch (user.getRole()){
            case ADMIN:
                return Buttons.buttonsForAdmin();
            case WORKER_WAREHOUSE:
                return Buttons.buttonsForWorkerWarehouse();
            default:
                return Buttons.buttonsForUser();
        }
    }

    public static SendMessage signOut(Long chatId){
        var telegramUser = telegramService.findUserByChatId(chatId);
        try{
            telegramService.deleteHistoryMessageById(telegramUser.getMessage().getId());
        }catch (Exception exception){
            exception.getMessage();
        }

        try{
            telegramService.deleteTelegramUserByChatId(chatId);
        }catch (Exception exception){
            exception.getMessage();
        }

        var sendMessage = new SendMessage();
        sendMessage.setText("Вы вышли из аккаунта");
        return sendMessage;
    }
}
