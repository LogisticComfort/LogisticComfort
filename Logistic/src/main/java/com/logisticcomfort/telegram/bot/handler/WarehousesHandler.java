package com.logisticcomfort.telegram.bot.handler;

import com.logisticcomfort.model.Product;
import com.logisticcomfort.model.User;
import com.logisticcomfort.repos.ProductRepo;
import com.logisticcomfort.service.TelegramService;
import com.logisticcomfort.service.UserService;
import com.logisticcomfort.service.WarehouseService;
import com.logisticcomfort.telegram.bot.InlKeyboardButton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class WarehousesHandler {

    private static TelegramService telegramService;
    private static UserService userService;
    private static WarehouseService warehouseService;
    private static ProductRepo productRepo;

    @Autowired
    public WarehousesHandler(TelegramService telegramService, UserService userService,
                             WarehouseService warehouseService, ProductRepo productRepo) {
        this.telegramService = telegramService;
        this.userService = userService;
        this.warehouseService = warehouseService;
        this.productRepo = productRepo;
    }

    public static SendMessage warehousesShow(Long chatId, String callBackCode){
        var user = telegramService.findUserByChatId(chatId).getUser();
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var role = user.getRole();
        switch (role){
            case ADMIN:
                inlineKeyboardMarkup = warehousesForAdmin(chatId, user, callBackCode);
                break;
            case USER: {
                inlineKeyboardMarkup = warehousesForUser(chatId, user, callBackCode);
                break;
            }

        }

        var sendMessage = new SendMessage();
        if (inlineKeyboardMarkup != null) {
            sendMessage.setText("Выберете склад: ");
        } else {
            sendMessage.setText("Отсутствуют необходимые для Вас склады");
        }
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private static InlineKeyboardMarkup warehousesForAdmin(Long chatId, User user, String callBackCode){
        var warehouses = user.getCompany().getWarehouses();

        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();

        for (var warehouse:warehouses) {
            var button = new InlineKeyboardButton();
            button.setText(warehouse.getName());
            button.setCallbackData(callBackCode + " " + warehouse.getCompany().getName() + " " + warehouse.getId());
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>(List.of(
                    button
            ));
            keyboardButtons.add(keyboardButtonsRow);
        }

        inlineKeyboardMarkup.setKeyboard(keyboardButtons);

        return inlineKeyboardMarkup;
    }

    private static InlineKeyboardMarkup warehousesForUser(Long chatId, User user, String callBackCode){
        var warehouse = user.getWarehouse();

        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();


        var button = new InlineKeyboardButton();
        button.setText(warehouse.getName());
        button.setCallbackData(callBackCode + " " + warehouse.getCompany().getName() + " " + warehouse.getId());
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>(List.of(
                button
        ));
        keyboardButtons.add(keyboardButtonsRow);


        inlineKeyboardMarkup.setKeyboard(keyboardButtons);

        return inlineKeyboardMarkup;
    }

    public static SendMessage showInformationAboutWarehouse(String text, Long chatId){
        var warehouseId = Integer.valueOf(text.split(" ")[2]);
        var warehouse = warehouseService.findWarehouseById(warehouseId);

        var products = productRepo.findAllByWarehouseOrderByVendorCodeAsc(warehouse);

        var inlineKeyboardMarkup = warehouseKeyboardWithProducts(products);

        var sendMessage = new SendMessage();
        sendMessage.setText("Товары вашего склада \"" + warehouse.getName() + "\":");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    private static InlineKeyboardMarkup warehouseKeyboardWithProducts(Set<Product> products){
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();

        List<InlineKeyboardButton> firstKeyboardButtonsRow = new ArrayList<>(List.of(
                new InlKeyboardButton.Builder().withText("name").withCallbackData("/").build(),
                new InlKeyboardButton.Builder().withText("vendor code").withCallbackData("/").build(),
                new InlKeyboardButton.Builder().withText("amount").withCallbackData("/").build()
        ));
        keyboardButtons.add(firstKeyboardButtonsRow);


        for (var product:products) {
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>(List.of(
                    new InlKeyboardButton.Builder().withText(product.getName()).withCallbackData("/").build(),
                    new InlKeyboardButton.Builder().withText(String.valueOf(product.getVendorCode())).withCallbackData("/").build(),
                    new InlKeyboardButton.Builder().withText(String.valueOf(product.getAmount())).withCallbackData("/").build()
            ));
            keyboardButtons.add(keyboardButtonsRow);
        }

        inlineKeyboardMarkup.setKeyboard(keyboardButtons);

        return inlineKeyboardMarkup;
    }
}
