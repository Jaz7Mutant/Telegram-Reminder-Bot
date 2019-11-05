package inputOutput;

import bot.BotController;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reminder.Reminder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class TelegramIO extends TelegramLongPollingBot implements UserIO {
    private static final String botUserName = "SimpleAutoReminderBot";
    private static final String botToken = "932793430:AAEe098f_fG7JYPrBupkqaxKRqcarQvUNKo";

    public TelegramIO(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    @Override
    public void listenCommands(Map<String, BiConsumer<String, String>> commands){
        return;
    }

    @Override
    public void showMessage(String message, String chatId) {
        try {
            execute(new SendMessage()
                    .setChatId(chatId)
                    .setText(message));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUserText(String prompt, String chatId) {
        return null;
    }

    @Override
    public void showOnClickButton(String header, String[] buttons, String chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
        int buttonsInRow = 3;
        if (buttons.length == 12){
            buttonsInRow = 3;
        }
        if (buttons.length > 20){
            buttonsInRow = 7;
        }
        for (int i = 0; i < buttons.length; i++){
            buttonsRow.add(new InlineKeyboardButton()
                    .setText(buttons[i])
                    .setCallbackData(Integer.toString(i)));
            if (buttonsRow.size() == buttonsInRow) {
                rowList.add(buttonsRow);
                buttonsRow = new ArrayList<>();
            }
        }
        if (buttonsRow.size() != 0){
            rowList.add(buttonsRow);
            //buttonsRow = new ArrayList<>();
        }
        inlineKeyboardMarkup.setKeyboard(rowList);

        try {
            execute(new SendMessage().setText(header).setChatId(chatId).setReplyMarkup(inlineKeyboardMarkup));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showList(String prompt, String[] elements, String chatId) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elements.length; i++){
            sb.append(i+1);
            sb.append(". ");
            sb.append(elements[i]);
            sb.append("\r\n");
        }
        String text = sb.toString();
        if (elements.length <= 0){
            text = "No elements";
        }
        try {
            execute(new SendMessage().setText(prompt).setChatId(chatId));
            execute(new SendMessage().setText(text).setChatId(chatId));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        // reminder.Note maker -> note Handler
        // В нем прокидываем сообщение пользователя или кнопке в doNextStep
        // если команда, то в noteHandler, если нет, то в State holder
        try {
            if (update.hasCallbackQuery()) {
                Reminder.userStates.get(Long.toString(update.getCallbackQuery().getMessage().getChatId()))
                        .doNextStep(update.getCallbackQuery().getData());
            } else if (update.hasMessage()) {
                BotController.parseCommand(update.getMessage().getText(), Long.toString(update.getMessage().getChatId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
