package com.logisticcomfort.telegram.bot;

import com.logisticcomfort.model.COMMANDS;
import com.logisticcomfort.model.Role;
import com.logisticcomfort.service.TelegramService;
import com.logisticcomfort.telegram.bot.handler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

// Аннотация @Component необходима, чтобы наш класс распознавался Spring, как полноправный Bean
//@Data
//@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {


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
        }else if (update.hasCallbackQuery()) {
            try {
                var chatId = update.getCallbackQuery().getMessage().getChatId();
                var command = update.getCallbackQuery().getData();

                SendMessage message = getCommandResponseByCallbackQuery(command, update.getCallbackQuery().getFrom(), chatId);
                message.enableHtml(true);
                message.setParseMode(ParseMode.HTML);
                message.setChatId(String.valueOf(chatId));
                execute(message);
            } catch (TelegramApiException e) {
//                log.error("", e);
            }
        }
    }



    private SendMessage getCommandResponseByCallbackQuery(String text, User user, Long chatId) throws TelegramApiException{

        try{
            if (text.substring(0, 10).equals("w12reh0use")) {
                return WarehousesHandler.showInformationAboutWarehouse(text, chatId);
            }
        }catch(Exception exception){
            exception.printStackTrace();
        }

        try{
            if (text.substring(0, 11).equals("a32dPr0duct")) {
                return ProductsHandler.addProduct(text, chatId);
            }
        }catch(Exception exception){
            exception.printStackTrace();
        }

        try {
            if (text.substring(0, 12).equals("ApplyProduct")) {
                return ApplyProductsHandler.showApplyProduct(text, chatId);
            }
        }catch(Exception exception){
            exception.printStackTrace();
        }

        if (text.startsWith("newUserWriteRole"))
            return PersonalHandler.newUserWriteWarehouse(text, chatId);

        if (text.startsWith("newUserWriteWarehouse"))
            return PersonalHandler.createNewUser(text, chatId);

        if(text.startsWith("Person")){
            return PersonalHandler.showEmployee(text, chatId);
        }

        if(text.startsWith("EditRole ")){
            return PersonalHandler.editRole(text, chatId);
        }

        if(text.startsWith("EditWarehouse ")){
            return PersonalHandler.editWarehouse(text, chatId);
        }

        if(text.startsWith("EditRoleWith ") || text.startsWith("EditWarehouseWith "))
            return PersonalHandler.allowChanges(text,chatId);


        return new SendMessage();
    }

    private SendMessage getCommandResponse(String text, User user, Long chatId) throws TelegramApiException {

        //Начальное состояние при авторизации
        if(telegramService.findUserByChatId(chatId) == null){
            var sendMessage = AuthenticationHandler.authentication(chatId);
            Buttons.setAuthenticationButtons(sendMessage);
            return sendMessage;
        }

        //Нажимает на кнопку Log In, чтобы выйти
        if(text.equals("Ввести данные заново")){
            var sendMessage = AuthenticationHandler.logIn(chatId);
            Buttons.setAuthenticationButtons(sendMessage);
            return sendMessage;
        }

        // Обычная авторизация
        if (telegramService.findUserByChatId(chatId).getUser() == null){
            var sendMessage = AuthenticationHandler.handleWriteLogin(chatId, text);

//            Buttons.setAuthenticationButtons(sendMessage);
            return sendMessage;
        }

        if(text.equals(COMMANDS.SIGN_OUT.getCommand())){
            return MainPageHandler.signOut(chatId);
        }

        if(!telegramService.findUserByChatId(chatId).getUser().getCompany().isActive()){
            var sendMessage = MainPageHandler.signOut(chatId);
            sendMessage.setText("Компания не активна. Введите данные заново");
            return sendMessage;
        }

        var role = telegramService.findUserByChatId(chatId).getUser().getRole();
        if(text.equals(COMMANDS.MAIN_PAGE.getCommand())){
            var sendMessage = MainPageHandler.showMenu(chatId, text);
            sendMessage.setText("Main page");
            return sendMessage;
        }

        if(text.equals(COMMANDS.WAREHOUSES.getCommand())){
            return WarehousesHandler.warehousesShow(chatId, "w12reh0use");
        }

        if(text.equals(COMMANDS.ADD_PRODUCT.getCommand())){
            return WarehousesHandler.warehousesShow(chatId, "a32dPr0duct");
        }

        if(text.equals(COMMANDS.APPLY_PRODUCT.getCommand()) && role == Role.ADMIN){
            return ApplyProductsHandler.showApplyProducts(text, chatId);
        }

        if (text.equals(COMMANDS.PERSONAL.getCommand()) && role == Role.ADMIN){
            return PersonalHandler.initialPersonalPage(text,chatId);
        }



        if (text.equals(COMMANDS.ADD_EMPLOYEE.getCommand()) && role == Role.ADMIN)
            return PersonalHandler.newUserMain(text,chatId);

        var message = telegramService.findMessageByTelegramUser(telegramService.findUserByChatId(chatId));

        if(message.getMessage().startsWith("UserAdd") && role == Role.ADMIN){
            var length = message.getMessage().split(" ").length;
            if (length == 1)
                return PersonalHandler.newUserWriteFullName(text, chatId);
            if (length == 2)
                return PersonalHandler.newUserWritePassword(text, chatId);
            if (length == 3)
                return PersonalHandler.newUserWriteEmail(text, chatId);
            if (length == 4)
                return PersonalHandler.newUserWriteRole(text, chatId);

        }

        if(message.getMessage().substring(0, 14).equals("AddProductName")){
            return ProductsHandler.addProductWithName(text, chatId);
        }

        if(message.getMessage().substring(0, 14).equals("AddProductCode")){
            return ProductsHandler.addProductWithCode(text, chatId);
        }

        if(message.getMessage().substring(0, 13).equals("AddProductEnd")){
            return ProductsHandler.addProductEnd(text, chatId);
        }

        if(message.getMessage().split(" ")[0].equals("APPLY_PRODUCT") && role == Role.ADMIN){
            return ApplyProductsHandler.applyProductAllowedOrNot(text, chatId);
        }

        if(text.startsWith("Allow changes") && role == Role.ADMIN)
            return EditHandler.editData(chatId);



        return MainPageHandler.mainPage(chatId);
    }

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
