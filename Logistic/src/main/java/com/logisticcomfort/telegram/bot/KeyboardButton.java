package com.logisticcomfort.telegram.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class KeyboardButton extends InlineKeyboardButton {

    public static class Builder{
        private KeyboardButton keyboardButton;

        public Builder(){
            keyboardButton = new KeyboardButton();
        }

        public Builder withText(String text){
            keyboardButton.setText(text);
            return this;
        }

        public Builder withCallbackData(String callbackData){
            keyboardButton.setCallbackData(callbackData);
            return this;
        }

        public KeyboardButton build(){
            return keyboardButton;
        }
    }
}
