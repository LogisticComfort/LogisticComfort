package com.logisticcomfort.telegram.bot;

import com.logisticcomfort.model.COMMANDS;
import com.logisticcomfort.model.TelegramUser;
import com.logisticcomfort.service.TelegramService;
import com.logisticcomfort.telegram.bot.handler.AuthenticationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Аннотация @Component необходима, чтобы наш класс распознавался Spring, как полноправный Bean
@Component
//@Data
//@Slf4j
public class Bot extends TelegramLongPollingBot {

//    private final String INFO_LABEL = "О чем канал?";
//    private final String ACCESS_LABEL = "Как получить доступ?";
//    private final String SUCCESS_LABEL = "Дайте полный доступ!";
//    private final String DEMO_LABEL = "Хочу демо-доступ на 3 дня";
//
//    private final SubscribersService subscribersService;
//

    @Autowired
    private TelegramService telegramService;

    private String name;

    private String token;

//    @Value("${telegram.chanel-id}")
    private String privateChannelId;

    @Override
    public String getBotUsername() {
        return "logisticComfort_bot";
    }

    @Override
    public String getBotToken() {
        return "2120089918:AAEjpGUTuSBMTF4Tv4TxLPI6kw9kVl3wXRo";
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()){
            String text = update.getMessage().getText();
            Long chat_id = update.getMessage().getChatId();

            var nowId =  update.getMessage().getMessageId();

            try {
                SendMessage message = getCommandResponse(text, update.getMessage().getFrom(), chat_id);
                message.enableHtml(true);
                message.setParseMode(ParseMode.HTML);
                message.setChatId(String.valueOf(chat_id));
                execute(message);

            } catch (TelegramApiException e) {
//                log.error("", e);
//                SendMessage message = handleNotFoundCommand();
//                message.setChatId(String.valueOf(chat_id));
            }
        }
    }

    //    @Override
//    public void onUpdateReceived(Update update) {
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            String text = update.getMessage().getText();
//            long chat_id = update.getMessage().getChatId();
//
//            try {
//                SendMessage message = getCommandResponse(text, update.getMessage().getFrom(), String.valueOf(chat_id));
//                message.enableHtml(true);
//                message.setParseMode(ParseMode.HTML);
//                message.setChatId(String.valueOf(chat_id));
//                execute(message);
//            } catch (TelegramApiException e) {
////                log.error("", e);
//                SendMessage message = handleNotFoundCommand();
//                message.setChatId(String.valueOf(chat_id));
//            }
//        } else if (update.hasCallbackQuery()) {
//            try {
//                SendMessage message = getCommandResponse(update.getCallbackQuery().getData(), update.getCallbackQuery().getFrom(), String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
//                message.enableHtml(true);
//                message.setParseMode(ParseMode.HTML);
//                message.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
//                execute(message);
//            } catch (TelegramApiException e) {
//                log.error("", e);
//            }
//        }
//    }

    private SendMessage getCommandResponse(String text, User user, Long chatId) throws TelegramApiException {

        if(telegramService.findUserByChatId(chatId) == null){
            return AuthenticationHandler.authentication(chatId);
        }
        if (text.equals(COMMANDS.LOG_IN_ACCOUNT.getCommand()) || telegramService.findUserByChatId(chatId).getUser() == null){
            return AuthenticationHandler.handleWriteLogin(chatId, text);
        }
        if (text.equals(COMMANDS.START.getCommand())) {
            return handleStartCommand(chatId);
        }


        return handleStartCommand(chatId);
    }


//
//    private SendMessage handleNotFoundCommand() {
//        SendMessage message = new SendMessage();
//        message.setText("Вы что-то сделали не так. Выберите команду:");
//        message.setReplyMarkup(getKeyboard());
//        return message;
//    }
//
//    private String getChatInviteLink() throws TelegramApiException {
//        ExportChatInviteLink exportChatInviteLink = new ExportChatInviteLink();
//        exportChatInviteLink.setChatId(privateChannelId);
//        return execute(exportChatInviteLink);
//    }
//
//    private SendMessage handleDemoCommand(String username, String id, String name, String chatId) throws TelegramApiException {
//        SendMessage message = new SendMessage();
//
//        if (subscribersService.isDemoAccess(chatId)) {
//            message.setText("Ссылка для доступа к закрытому каналу: " + getChatInviteLink() + " \nЧерез 3 дня вы будете исключены из канала");
//
//            addInfoSubscriberToDb(username, chatId, name, TypeSubscribe.DEMO);
//        } else {
//            message.setText("Вы уже получали демо-доступ");
//        }
//
//        message.setReplyMarkup(getKeyboard());
//
//        return message;
//    }
//
//    private Subscriber addInfoSubscriberToDb(String username, String chatId, String name,
//                                             TypeSubscribe typeSubscribe) {
//        Subscriber subscriber = new Subscriber();
//        subscriber.setTypeSubscribe(typeSubscribe);
//        subscriber.setTelegramId(chatId);
//        subscriber.setName(name);
//        subscriber.setLogin(username);
//        subscriber.setStartDate(LocalDateTime.now());
//
//        return subscribersService.add(subscriber);
//    }
//
    private SendMessage handleStartCommand(Long chatId) {
        SendMessage message = new SendMessage();
        if (telegramService.findUserByChatId(chatId) == null){
            List<InlineKeyboardButton> row = List.of(
                    new InlKeyboardButton.Builder().withText("Log in").withCallbackData(COMMANDS.LOG_IN_ACCOUNT.getCommand()).build()
            );
            List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();
            keyboardButtons.add(row);

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(keyboardButtons);

            message.setReplyMarkup(inlineKeyboardMarkup);
        }

        message.setText("Доступные команды:");
        return message;
    }
//
//    private SendMessage handleInfoCommand() {
//        SendMessage message = new SendMessage();
//        message.setText("Это канал о самых вкусных пирожочках");
//        message.setReplyMarkup(getKeyboard());
//        return message;
//    }
//
//    private SendMessage handleAccessCommand() {
//        SendMessage message = new SendMessage();
//        message.setText("Чтобы получить полный доступ, вам надо сказать волшебное слово");
//        message.setReplyMarkup(getKeyboard());
//        return message;
//    }
//
//    private SendMessage handleSuccessCommand() {
//        SendMessage message = new SendMessage();
//        message.setText("После проверки вам выдадут полный доступ");
//        message.setReplyMarkup(getKeyboard());
//        return message;
//    }
//
//    private InlineKeyboardMarkup getKeyboard() {
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//
//        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
//        inlineKeyboardButton.setText(INFO_LABEL);
//        inlineKeyboardButton.setCallbackData(COMMANDS.INFO.getCommand());
//
//        InlineKeyboardButton inlineKeyboardButtonAccess = new InlineKeyboardButton();
//        inlineKeyboardButtonAccess.setText(ACCESS_LABEL);
//        inlineKeyboardButtonAccess.setCallbackData(COMMANDS.ACCESS.getCommand());
//
//        InlineKeyboardButton inlineKeyboardButtonDemo = new InlineKeyboardButton();
//        inlineKeyboardButtonDemo.setText(DEMO_LABEL);
//        inlineKeyboardButtonDemo.setCallbackData(COMMANDS.DEMO.getCommand());
//
//        InlineKeyboardButton inlineKeyboardButtonSuccess = new InlineKeyboardButton();
//        inlineKeyboardButtonSuccess.setText(SUCCESS_LABEL);
//        inlineKeyboardButtonSuccess.setCallbackData(COMMANDS.SUCCESS.getCommand());
//
//        List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();
//
//        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
//        keyboardButtonsRow1.add(inlineKeyboardButton);
//        keyboardButtonsRow1.add(inlineKeyboardButtonAccess);
//
//        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
//        keyboardButtonsRow2.add(inlineKeyboardButtonSuccess);
//
//        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
//        keyboardButtonsRow3.add(inlineKeyboardButtonDemo);
//
//        keyboardButtons.add(keyboardButtonsRow1);
//        keyboardButtons.add(keyboardButtonsRow3);
//        keyboardButtons.add(keyboardButtonsRow2);
//
//        inlineKeyboardMarkup.setKeyboard(keyboardButtons);
//
//        return inlineKeyboardMarkup;
//    }
}
