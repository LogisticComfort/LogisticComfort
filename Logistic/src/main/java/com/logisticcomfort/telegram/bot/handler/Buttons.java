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
        buttonLogIn.setText("Ввести данные заново");

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(buttonLogIn);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(List.of(keyboardRow));

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

    public static SendMessage buttonsForAdmin(){
        var sendMessage = new SendMessage();

        var firstKeyboardRow = new KeyboardRow();
        firstKeyboardRow.addAll(List.of(
                COMMANDS.WAREHOUSES.getCommand(),
                COMMANDS.APPLY_PRODUCT.getCommand(),
                COMMANDS.PERSONAL.getCommand()
        ));

        var secondKeyboardRow = new KeyboardRow();
        secondKeyboardRow.addAll(List.of(
                COMMANDS.ADD_PRODUCT.getCommand(),
                COMMANDS.SIGN_OUT.getCommand()
        ));

        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(List.of(
                firstKeyboardRow,
                secondKeyboardRow
        ));

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    public static SendMessage buttonsForWorkerWarehouse(){
        var sendMessage = new SendMessage();

        var firstKeyboardRow = new KeyboardRow();
        firstKeyboardRow.addAll(List.of(
                COMMANDS.WAREHOUSES.getCommand()
        ));

        var secondKeyboardRow = new KeyboardRow();
        secondKeyboardRow.addAll(List.of(
                COMMANDS.ADD_PRODUCT.getCommand(),
                COMMANDS.SIGN_OUT.getCommand()
        ));

        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(List.of(
                firstKeyboardRow,
                secondKeyboardRow
        ));

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    public static SendMessage buttonsForUser(){
        var sendMessage = new SendMessage();

        var firstKeyboardRow = new KeyboardRow();

        var secondKeyboardRow = new KeyboardRow();
        secondKeyboardRow.addAll(List.of(
                COMMANDS.SIGN_OUT.getCommand()
        ));

        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(List.of(
                firstKeyboardRow,
                secondKeyboardRow
        ));

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }
}
