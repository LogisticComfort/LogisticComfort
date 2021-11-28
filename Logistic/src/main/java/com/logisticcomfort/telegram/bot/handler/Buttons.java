package com.logisticcomfort.telegram.bot.handler;

import com.logisticcomfort.model.COMMANDS;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class Buttons {

    public static void setAuthenticationButtons(SendMessage sendMessage){
        var buttonLogIn = new KeyboardButton();
        buttonLogIn.setText(COMMANDS.LOG_IN_ACCOUNT.getCommand());

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(buttonLogIn);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(List.of(keyboardRow));
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendMessage.setText("Please");
    }
}
