package com.logisticcomfort.telegram.bot.handler;

import com.logisticcomfort.model.Product;
import com.logisticcomfort.repos.CompanyRepo;
import com.logisticcomfort.service.ProductService;
import com.logisticcomfort.service.TelegramService;
import com.logisticcomfort.service.UserService;
import com.logisticcomfort.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class ProductsHandler {

    private static TelegramService telegramService;
    private static UserService userService;
    private static WarehouseService warehouseService;
    private static ProductService productService;
    private static CompanyRepo companyRepo;

    @Autowired
    public ProductsHandler(TelegramService telegramService, UserService userService,
                           WarehouseService warehouseService, ProductService productService,
                           CompanyRepo companyRepo) {
        this.telegramService = telegramService;
        this.userService = userService;
        this.warehouseService = warehouseService;
        this.productService = productService;
        this.companyRepo = companyRepo;
    }

    public static SendMessage addProduct(String text, Long chatId){
        var warehouseId = Integer.valueOf(text.split(" ")[2]);
        var warehouse = warehouseService.findWarehouseById(warehouseId);

        var sendMessage = new SendMessage();
        sendMessage = MainPageHandler.mainPage(chatId);
        sendMessage.setText(String.format("Вы выбрали склад \"%s\" \n Введите пожалуйста название товара", warehouse.getName()));

        var user = telegramService.findUserByChatId(chatId);
        var historyMessage = telegramService.findMessageByTelegramUser(user);

        historyMessage.setMessage("AddProductName " + warehouse.getId());
        telegramService.saveHistoryMessage(historyMessage);

        return sendMessage;
    }

    public static SendMessage addProductWithName(String text, Long chatId){
        var user = telegramService.findUserByChatId(chatId);
        var historyMessage = telegramService.findMessageByTelegramUser(user);
        var warehouseId = historyMessage.getMessage().split(" ")[1];

        historyMessage.setMessage("AddProductCode " + warehouseId + " " + text);
        telegramService.saveHistoryMessage(historyMessage);

        var sendMessage = new SendMessage();
        sendMessage = MainPageHandler.mainPage(chatId);
        sendMessage.setText(String.format("Товар с именем \"%s\" \n Введите пожалуйста артикул товара", text));

        return sendMessage;
    }

    public static SendMessage addProductWithCode(String text, Long chatId){
        var user = telegramService.findUserByChatId(chatId);
        var historyMessage = telegramService.findMessageByTelegramUser(user);
        var warehouseIdWithProductName = historyMessage.getMessage().split(" ")[1] + " " + historyMessage.getMessage().split(" ")[2];

        historyMessage.setMessage("AddProductEnd " + warehouseIdWithProductName + " " + text);
        telegramService.saveHistoryMessage(historyMessage);

        var sendMessage = new SendMessage();
        sendMessage = MainPageHandler.mainPage(chatId);
        sendMessage.setText(String.format("Товар с именем \"%s\" и артикулом \"%s\" \n Введите пожалуйста количество товара",
                historyMessage.getMessage().split(" ")[2], text));

        return sendMessage;
    }

//    public static SendMessage addProductWithAmount(String text, Long chatId){
//        var user = telegramService.findUserByChatId(chatId);
//        var historyMessage = telegramService.findMessageByTelegramUser(user);
//        var warehouseIdWithProductNameAndCode = historyMessage.getMessage().split(" ")[1] + " "
//                + historyMessage.getMessage().split(" ")[2] + " " + historyMessage.getMessage().split(" ")[3];
//
//        historyMessage.setMessage("AddProductEnd " + warehouseIdWithProductNameAndCode + " " + text);
//        telegramService.saveHistoryMessage(historyMessage);
//
//        var sendMessage = new SendMessage();
//        sendMessage = MainPageHandler.mainPage(chatId);
//        sendMessage.setText(String.format("Товар с именем \"%s\", артикулом \"%s\" и количеством \"%s\" \n Введите пожалуйста количество товара",
//                historyMessage.getMessage().split(" ")[2], historyMessage.getMessage().split(" ")[3], text));
//
//        return sendMessage;
//    }

    public static SendMessage addProductEnd(String text, Long chatId){
        var user = telegramService.findUserByChatId(chatId);
        var historyMessage = telegramService.findMessageByTelegramUser(user);

        var message = historyMessage.getMessage().split(" ");
        var warehouse = warehouseService.findWarehouseById(Integer.valueOf(message[1]));
        var nameProduct = message[2];
        var vendorCodeProduct = Long.valueOf(message[3]);
        var amountOfProduct = Long.valueOf(text);
//        var comp = userService.getCompany(telegramService.findUserByChatId(chatId).getUser());

        productService.addProductInApply(new Product(nameProduct, amountOfProduct, vendorCodeProduct, warehouse), warehouse, userService.getCompany(telegramService.findUserByChatId(chatId).getUser()));

        historyMessage.setMessage("");
        telegramService.saveHistoryMessage(historyMessage);

        var sendMessage = new SendMessage();
        sendMessage = MainPageHandler.mainPage(chatId);
        sendMessage.setText(String.format("Товар с именем \"%s\", артикулом \"%s\" и количеством \"%s\" ДОБАВЛЕН",
                nameProduct, vendorCodeProduct, text));

        return sendMessage;
    }
}
