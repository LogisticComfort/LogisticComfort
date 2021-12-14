package com.logisticcomfort.telegram.bot.handler;

import com.logisticcomfort.model.HistoryMessage;
import com.logisticcomfort.model.Role;
import com.logisticcomfort.model.User;
import com.logisticcomfort.repos.CompanyRepo;
import com.logisticcomfort.service.ProductService;
import com.logisticcomfort.service.TelegramService;
import com.logisticcomfort.service.UserService;
import com.logisticcomfort.service.WarehouseService;
import com.logisticcomfort.telegram.bot.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class EditHandler {

    private static TelegramService telegramService;
    private static UserService userService;
    private static WarehouseService warehouseService;
    private static ProductService productService;
    private static CompanyRepo companyRepo;

    private static Bot bot;

    @Autowired
    public EditHandler(TelegramService telegramService, UserService userService,
                           WarehouseService warehouseService, ProductService productService,
                           CompanyRepo companyRepo, Bot bot) {
        this.telegramService = telegramService;
        this.userService = userService;
        this.warehouseService = warehouseService;
        this.productService = productService;
        this.companyRepo = companyRepo;
        this.bot = bot;
    }

    public static SendMessage editData(Long chatId){
        var historyMessage = telegramService.findMessageByChatId(chatId);
        var array = historyMessage.getMessage().split(" ");

        var user = userService.findUserById(Long.parseLong(array[1]));

        if (array[2].equals("Role"))
            return editRole(chatId, user, historyMessage, array);

        if (array[2].equals("Warehouse"))
            return editWarehouse(chatId, user, historyMessage, array);

        return new SendMessage("Нет методов для изменения таких данных", String.valueOf(chatId));
    }

    private static SendMessage editWarehouse(Long chatId, User user, HistoryMessage historyMessage, String[] array) {
        var warehouse = warehouseService.findWarehouseById(Long.parseLong(array[3]));
        user.setWarehouse(warehouse);
        userService.saveUser(user);

        historyMessage.setMessage("");
        telegramService.saveHistoryMessage(historyMessage);

        var sendMessage = MainPageHandler.mainPage(chatId);
        sendMessage.setText("Данные изменены!");
        return sendMessage;
    }

    private static SendMessage editRole(Long chatId, User user, HistoryMessage historyMessage, String[] array) {
        Role role = Role.valueOf(array[3]);
        user.editRole(role);
        userService.saveUser(user);

        historyMessage.setMessage("");
        telegramService.saveHistoryMessage(historyMessage);

        var sendMessage = MainPageHandler.mainPage(chatId);
        sendMessage.setText("Данные изменены!");
        return sendMessage;
    }
}
