package com.logisticcomfort.telegram.bot.handler;

import com.logisticcomfort.model.COMMANDS;
import com.logisticcomfort.model.Role;
import com.logisticcomfort.model.Warehouse;
import com.logisticcomfort.repos.CompanyRepo;
import com.logisticcomfort.service.ProductService;
import com.logisticcomfort.service.TelegramService;
import com.logisticcomfort.service.UserService;
import com.logisticcomfort.service.WarehouseService;
import com.logisticcomfort.telegram.bot.Bot;
import com.logisticcomfort.telegram.bot.InlKeyboardButton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static com.logisticcomfort.model.StatusProduct.EXPECTS;

@Component
public class PersonalHandler {

    private static TelegramService telegramService;
    private static UserService userService;
    private static WarehouseService warehouseService;
    private static ProductService productService;
    private static CompanyRepo companyRepo;

    private static Bot bot;

    @Autowired
    public PersonalHandler(TelegramService telegramService, UserService userService,
                           WarehouseService warehouseService, ProductService productService,
                           CompanyRepo companyRepo, Bot bot) {
        this.telegramService = telegramService;
        this.userService = userService;
        this.warehouseService = warehouseService;
        this.productService = productService;
        this.companyRepo = companyRepo;
        this.bot = bot;
    }


    public static SendMessage initialPersonalPage(String text, Long chatId) {
        var sendMessage = new SendMessage();
        var company = telegramService.findUserByChatId(chatId).getUser().getCompany();
        sendMessage.setText(String.format("Персонал компании \"%s\":", company.getName()));

        var personal = userService.findAllByCompanyOrderByIdAsc(telegramService.findUserByChatId(chatId).getUser());

        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();


        for (var person : personal) {
            var button = new InlineKeyboardButton();
            button.setText(person.getFullName());
            button.setCallbackData("Person " + person.getId());
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>(List.of(
                    button
            ));
            keyboardButtons.add(keyboardButtonsRow);
        }

        inlineKeyboardMarkup.setKeyboard(keyboardButtons);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        sendMessage.enableHtml(true);
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setChatId(String.valueOf(chatId));

        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        sendMessage.setText(".");
        //Создаем кнопки
        var buttonAddEmployee = new KeyboardButton();
        buttonAddEmployee.setText("ADD EMPLOYEE");

        var buttonMainPage = new KeyboardButton();
        buttonMainPage.setText(COMMANDS.MAIN_PAGE.getCommand());

        var firstKeyboardRow = new KeyboardRow();
        firstKeyboardRow.add(buttonAddEmployee);

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

    public static SendMessage showEmployee(String text, Long chatId) {
        var employee = userService.findUserById(Long.parseLong(text.split(" ")[1]));
        var sendMessage = new SendMessage();
        sendMessage.setText(
                String.format(
                        "Имя: %s \nЛогин: %s \nПароль: %s \nEmail: %s \nРоль: %s \nСклад: "
                        , employee.getFullName(), employee.getUsername(), employee.getPassword(), employee.getEmail(),
                        employee.getRole())
        );

        try {
            sendMessage.setText(sendMessage.getText() + employee.getWarehouse().getName());
        } catch (Exception exception) {
            sendMessage.setText(sendMessage.getText() + null);
        }


        //Создаем inline кнопки
        var firstInlineKeyboardRow = List.of(
                (InlineKeyboardButton) new InlKeyboardButton.Builder().withText("Поменять роль").withCallbackData("EditRole " + employee.getId()).build()
        );
        var secondInlineKeyboardRow = List.of(
                (InlineKeyboardButton) new InlKeyboardButton.Builder().withText("Поменять склад").withCallbackData("EditWarehouse " + employee.getId()).build()
        );
        var keyboardButtons = List.of(firstInlineKeyboardRow, secondInlineKeyboardRow);

        var inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboardButtons);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        sendMessage.enableHtml(true);
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setChatId(String.valueOf(chatId));

        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


        //Создаем keyboard кнопки
        sendMessage.setText(".");
        var buttonMainPage = new KeyboardButton();
        buttonMainPage.setText(COMMANDS.MAIN_PAGE.getCommand());

        var firstKeyboardRow = new KeyboardRow();
        firstKeyboardRow.add(buttonMainPage);


        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(List.of(
                firstKeyboardRow
        ));

        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }

    public static SendMessage editRole(String text, Long chatId) {
        var employee = userService.findUserById(Long.parseLong(text.split(" ")[1]));

        var sendMessage = new SendMessage();
        sendMessage.setText(String.format("У работника \"%s\"\nсейчас роль: \"%s\"\nВыберете роль, которую вы хотите дать работнику:",
                employee.getFullName(), employee.getRole()));

        List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();
        for (var role: Role.values()) {
            var inlineKeyboardRow = List.of(
                    (InlineKeyboardButton) new InlKeyboardButton.Builder().withText(role.name()).withCallbackData("EditRoleWith " + employee.getId() + " " + role.name()).build()
            );
            keyboardButtons.add(inlineKeyboardRow);
        }

        var inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboardButtons);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    public static SendMessage editWarehouse(String text, Long chatId) {
        var employee = userService.findUserById(Long.parseLong(text.split(" ")[1]));

        var sendMessage = new SendMessage();
        sendMessage.setText(String.format("У работника \"%s\"\nсейчас склад: \"%s\"\nВыберете склад, к которому вы хотите присоединить работника:",
                employee.getFullName(), employee.getRole()));

        List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();
        for (var warehouse: employee.getCompany().getWarehouses()) {
            var inlineKeyboardRow = List.of(
                    (InlineKeyboardButton) new InlKeyboardButton.Builder().withText(warehouse.getName()).withCallbackData("EditWarehouseWith " + employee.getId() + " " + warehouse.getId()).build()
            );
            keyboardButtons.add(inlineKeyboardRow);
        }

        var inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboardButtons);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    public static SendMessage allowChanges(String text, Long chatId){
        var employee = userService.findUserById(Long.parseLong(text.split(" ")[1]));
        var array = text.split(" ");
        Warehouse warehouse = null;
        if (!array[0].equals("EditRoleWith"))
            warehouse = warehouseService.findWarehouseById(Long.parseLong(array[2]));

        var string = array[0].equals("EditRoleWith") ? String.format("Role.%s -> Role.%s", employee.getRole(), array[2])
                : String.format("Warehouse.%s -> Warehouse.%s", employee.getWarehouse() == null ? null : employee.getWarehouse().getName(), warehouse.getName()) ;

        var sendMessage = new SendMessage();
        sendMessage.setText(String.format("Вы уверены, что хотите изменить у %s следующее:\n %s",
                employee.getFullName(), string));

        var historyMessage = telegramService.findMessageByChatId(chatId);
        var field = array[0].equals("EditRoleWith") ? "Role" : "Warehouse";
        var value = array[0].equals("EditRoleWith") ? array[2] : String.valueOf(warehouse.getId());
        historyMessage.setMessage(String.format("EditUser %s %s %s", employee.getId(), field, value));
        telegramService.saveHistoryMessage(historyMessage);

        //Создаем keyboard кнопки
        var agreeEdit = new KeyboardButton("Allow changes");
        var notAgreeEdit = new KeyboardButton("Not allow changes");
        var buttonMainPage = new KeyboardButton(COMMANDS.MAIN_PAGE.getCommand());

        var firstKeyboardRow = new KeyboardRow(List.of(agreeEdit, notAgreeEdit));
        var secondKeyboardRow = new KeyboardRow(List.of(buttonMainPage));

        var replyKeyboardMarkup = new ReplyKeyboardMarkup(List.of(firstKeyboardRow, secondKeyboardRow));

        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }
}
