package com.logisticcomfort.telegram.bot.handler;

import com.logisticcomfort.model.ApplyProduct;
import com.logisticcomfort.model.COMMANDS;
import com.logisticcomfort.model.Product;
import com.logisticcomfort.model.StatusProduct;
import com.logisticcomfort.repos.CompanyRepo;
import com.logisticcomfort.service.ProductService;
import com.logisticcomfort.service.TelegramService;
import com.logisticcomfort.service.UserService;
import com.logisticcomfort.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.logisticcomfort.model.StatusProduct.EXPECTS;

@Component
public class ApplyProductsHandler {

    private static TelegramService telegramService;
    private static UserService userService;
    private static WarehouseService warehouseService;
    private static ProductService productService;
    private static CompanyRepo companyRepo;

    @Autowired
    public ApplyProductsHandler(TelegramService telegramService, UserService userService,
                           WarehouseService warehouseService, ProductService productService,
                           CompanyRepo companyRepo) {
        this.telegramService = telegramService;
        this.userService = userService;
        this.warehouseService = warehouseService;
        this.productService = productService;
        this.companyRepo = companyRepo;
    }

    public static SendMessage showApplyProducts(String text, Long chatId){
        var sendMessage = new SendMessage();
        sendMessage.setText("Apply products: ");

        var user = telegramService.findUserByChatId(chatId).getUser();
        var applyProducts = user.getCompany().getApplyProducts();

        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();

        //Берем только те продукты, которые имеют статус EXPECTS
        for (var product:applyProducts) {
            if (product.getStatus() == EXPECTS){
                var button = new InlineKeyboardButton();
                button.setText(product.getName() + " " + "Vendor code: " + product.getVendorCode());
                button.setCallbackData("ApplyProduct " + product.getId());
                List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>(List.of(
                        button
                ));
                keyboardButtons.add(keyboardButtonsRow);
            }
        }

        //Проверяем на наличие apply products, если их нет, то добавляем в sendMessage подпись об их отсутствии
        if (keyboardButtons.size() == 0)
            sendMessage.setText(sendMessage.getText() + "\n no apply products");
        else {
            inlineKeyboardMarkup.setKeyboard(keyboardButtons);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }

        return sendMessage;
    }

    public static SendMessage showApplyProduct(String text, Long chatId){
        var applyProduct = productService.findApplyProductById(Long.parseLong(text.split(" ")[1]));

        var user = telegramService.findUserByChatId(chatId);
        var historyMessage = telegramService.findMessageByTelegramUser(user);

        historyMessage.setMessage("APPLY_PRODUCT " + String.valueOf(applyProduct.getId()));
        telegramService.saveHistoryMessage(historyMessage);

        var sendMessage = new SendMessage();
        sendMessage.setText(String.format("Information about the apply product \"%s\"", applyProduct.getName()));
        sendMessage.setText(sendMessage.getText() + String.format("\n \n name: %s\n vendor code: %s\n amount: %s" +
                "\n warehouse name: %s\n description: %s", applyProduct.getName(), applyProduct.getVendorCode(), applyProduct.getAmountAdd(),
                applyProduct.getWarehouseName(), applyProduct.getDescription()));

        //Создаем кнопки
        var buttonMainPage = new KeyboardButton();
        buttonMainPage.setText(COMMANDS.MAIN_PAGE.getCommand());

        var buttonAllowed = new KeyboardButton();
        buttonAllowed.setText("ALLOWED");

        var buttonNotAllowed = new KeyboardButton();
        buttonNotAllowed.setText("NOT ALLOWED");


        var firstKeyboardRow = new KeyboardRow();
        firstKeyboardRow.add(buttonAllowed);
        firstKeyboardRow.add(buttonNotAllowed);

        var secondKeyboardRow = new KeyboardRow();
        secondKeyboardRow.add(buttonMainPage);


        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(List.of(
                firstKeyboardRow,
                secondKeyboardRow
        ));

        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }

    public static SendMessage applyProductAllowedOrNot(String text, Long chatId){
        var historyMessage = telegramService.findMessageByChatId(chatId);
        var applyProduct = productService.findApplyProductById(Long.parseLong(historyMessage.getMessage().split(" ")[1]));

        var sendMessage = new SendMessage();
        sendMessage = MainPageHandler.mainPage(chatId);

        if (text.equals("ALLOWED")){
            applyProduct.setStatus(StatusProduct.AllOWED);
            sendMessage.setText("Вы одобрили продукт " + applyProduct.getName());

            var warehouse = warehouseService.findWarehouseById((long)applyProduct.getWarehousesId());
            var productNew = new Product(applyProduct.getName(), applyProduct.getAmountAdd(), applyProduct.getVendorCode(), warehouse);

            productService.saveProduct(productNew, warehouse);
        } else if (text.equals("NOT ALLOWED")){
            applyProduct.setStatus(StatusProduct.NOT_ALLOWED);
            sendMessage.setText("Вы не одобрили продукт " + applyProduct.getName());
        }

        productService.saveApplyProduct(applyProduct);

        return sendMessage;
    }

}
