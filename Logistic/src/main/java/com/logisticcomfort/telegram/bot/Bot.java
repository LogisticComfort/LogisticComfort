package com.logisticcomfort.telegram.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

// Аннотация @Component необходима, чтобы наш класс распознавался Spring, как полноправный Bean
@Component
// Наследуемся от TelegramLongPollingBot - абстрактного класса Telegram API
public class Bot extends TelegramLongPollingBot {
    // Аннотация @Value позволяет задавать значение полю путем считывания из application.yaml
    private String botUsername;

    private String botToken;

    /* Перегружаем метод интерфейса LongPollingBot
    Теперь при получении сообщения наш бот будет отвечать сообщением Hi!
     */
    @Override
    public void onUpdateReceived(Update update) {
//        // We check if the update has a message and the message has text
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
//            message.setChatId(update.getMessage().getChatId().toString());
//            message.setText(update.getMessage().getText());
//
//            try {
//                execute(message); // Call method to send the message
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//        }

        List<InlineKeyboardButton> row = List.of(
                new KeyboardButton.Builder().withText("test button").withCallbackData("callbackData").build(),
                new KeyboardButton.Builder().withText("test button2").withCallbackData("callbackData2").build());

        List<InlineKeyboardButton> rowTwo = List.of(
                new KeyboardButton.Builder().withText("test button3").withCallbackData("callbackData3").build(),
                new KeyboardButton.Builder().withText("test button4").withCallbackData("callbackData4").build());

        List<List<InlineKeyboardButton>> keyboard = List.of(row, rowTwo);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(update.getMessage().getText());

        var markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(markup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    @Override
    public String getBotUsername() {
        return "logisticComfort_bot";
    }

    @Override
    public String getBotToken() {
        return "2120089918:AAEjpGUTuSBMTF4Tv4TxLPI6kw9kVl3wXRo";
    }
}
