package com.logisticcomfort.telegram.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class InlKeyboardButton extends InlineKeyboardButton {

    public static class Builder{
        private InlKeyboardButton keyboardButton;

        public Builder(){
            keyboardButton = new InlKeyboardButton();
        }

        public Builder withText(String text){
            keyboardButton.setText(text);
            return this;
        }

        public Builder withCallbackData(String callbackData){
            keyboardButton.setCallbackData(callbackData);
            return this;
        }

        public InlKeyboardButton build(){
            return keyboardButton;
        }
    }
}
