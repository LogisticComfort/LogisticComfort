package com.logisticcomfort.telegram.bot.handler;

import com.logisticcomfort.model.COMMANDS;
import com.logisticcomfort.model.Role;
import com.logisticcomfort.model.User;
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
        buttonAddEmployee.setText(COMMANDS.ADD_EMPLOYEE.getCommand());

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

    public static SendMessage newUserMain(String text, Long chatId){
        var sendMessage = MainPageHandler.mainPage(chatId);
        sendMessage.setText("Введите логин: ");

        var historyMessage = telegramService.findMessageByChatId(chatId);
        historyMessage.setMessage("UserAdd");
        telegramService.saveHistoryMessage(historyMessage);
        return sendMessage;
    }

    public static SendMessage newUserWriteFullName(String text, Long chatId){
        if (userService.findUserByUsername(text) != null)
            return new SendMessage(String.valueOf(chatId), "Пользователь с таким логином уже есть. \nПопробуйте ещё раз.");

        var historyMessage = telegramService.findMessageByChatId(chatId);
        historyMessage.setMessage(historyMessage.getMessage() + " " + text);
        telegramService.saveHistoryMessage(historyMessage);

        return new SendMessage(String.valueOf(chatId), "Введите пожалуйста полное имя пользователя: ");
    }

    public static SendMessage newUserWritePassword(String text, Long chatId){
        var historyMessage = telegramService.findMessageByChatId(chatId);
        historyMessage.setMessage(historyMessage.getMessage() + " " + text);
        telegramService.saveHistoryMessage(historyMessage);

        return new SendMessage(String.valueOf(chatId), "Введите пожалуйста пароль для пользователя: ");
    }

    public static SendMessage newUserWriteEmail(String text, Long chatId){
        var historyMessage = telegramService.findMessageByChatId(chatId);
        historyMessage.setMessage(historyMessage.getMessage() + " " + text);
        telegramService.saveHistoryMessage(historyMessage);

        return new SendMessage(String.valueOf(chatId), "Введите пожалуйста email для пользователя: ");
    }

    public static SendMessage newUserWriteRole(String text, Long chatId){
        String EMAIL_REGEX = "([a-zA-Z0-9]+(?:[._+-][a-zA-Z0-9]+)*)@([a-zA-Z0-9]+(?:[.-][a-zA-Z0-9]+)*[.][a-zA-Z]{2,})";
        if(!text.matches(EMAIL_REGEX))
            return new SendMessage(String.valueOf(chatId), "Неправильный формат email.\nПопробуйте ещё раз ввести email:" );
        var historyMessage = telegramService.findMessageByChatId(chatId);
        historyMessage.setMessage(historyMessage.getMessage() + " " + text);
        telegramService.saveHistoryMessage(historyMessage);

        var sendMessage = new SendMessage(String.valueOf(chatId), "Введите пожалуйста email для пользователя: ");
        List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();
        for (var role: Role.values()) {
            var inlineKeyboardRow = List.of(
                    (InlineKeyboardButton) new InlKeyboardButton.Builder().withText(role.name()).withCallbackData("newUserWriteRole " + role.name()).build()
            );
            keyboardButtons.add(inlineKeyboardRow);
        }

        var inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboardButtons);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return sendMessage;
    }

    public static SendMessage newUserWriteWarehouse(String text, Long chatId){
        var historyMessage = telegramService.findMessageByChatId(chatId);
        var company = telegramService.findUserByChatId(chatId).getUser().getCompany();
        historyMessage.setMessage(historyMessage.getMessage() + " " + text.split(" ")[1]);
        telegramService.saveHistoryMessage(historyMessage);

        var sendMessage = new SendMessage(String.valueOf(chatId), "Введите пожалуйста email для пользователя: ");
        List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();
        for (var warehouse: company.getWarehouses()) {
            var inlineKeyboardRow = List.of(
                    (InlineKeyboardButton) new InlKeyboardButton.Builder().withText(warehouse.getName()).withCallbackData("newUserWriteWarehouse " + warehouse.getId()).build()
            );
            keyboardButtons.add(inlineKeyboardRow);
        }

        var inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboardButtons);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return sendMessage;
    }

    public static SendMessage createNewUser(String text, Long chatId){
        var historyMessage = telegramService.findMessageByChatId(chatId);
        var array = historyMessage.getMessage().split(" ");
        var user = new User();
        try {
            user.setUsername(array[1]);
            user.setFullName(array[2]);
            user.setPassword(array[3]);
            user.setEmail(array[4]);
            user.editRole(Role.valueOf(array[5]));
            user.setWarehouse(warehouseService.findWarehouseById(Long.parseLong(text.split(" ")[1])));
            user.setActive(true);
            user.setCompany(telegramService.findUserByChatId(chatId).getUser().getCompany());
            userService.saveUser(user);
        }catch (Exception exception){
            exception.printStackTrace();
            return new SendMessage(String.valueOf(chatId), "В данных ошибка");
        }

        historyMessage.setMessage("");
        telegramService.saveHistoryMessage(historyMessage);
        
        return new SendMessage(String.valueOf(chatId), "Well done!");
    }
}
