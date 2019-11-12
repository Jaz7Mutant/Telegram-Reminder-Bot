package com.jaz7.inputOutput;

import com.jaz7.bot.BotController;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.jaz7.reminder.Reminder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TelegramIO extends TelegramLongPollingBot implements UserIO {
    private static final String botUserName = "SimpleAutoReminderBot";
    private static final String botToken = "932793430:AAEe098f_fG7JYPrBupkqaxKRqcarQvUNKo";
    private static final Logger LOGGER = Logger.getLogger(TelegramIO.class.getSimpleName());

    public TelegramIO(DefaultBotOptions botOptions) {
        super(botOptions);
        LOGGER.info("Init telegram IO");
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
            LOGGER.info(chatId + ": Message has been send");
        } catch (TelegramApiException e) {
            LOGGER.log(Level.WARNING, chatId + ": Error sending message", e);
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
        }
        inlineKeyboardMarkup.setKeyboard(rowList);

        try {
            execute(new SendMessage().setText(header).setChatId(chatId).setReplyMarkup(inlineKeyboardMarkup));
            LOGGER.info(chatId + ": Buttons shown successfully");
        } catch (TelegramApiException e) {
            LOGGER.log(Level.WARNING, chatId + ": Error showing buttons", e);
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
            LOGGER.info(chatId + ": List shown successfully");
        } catch (TelegramApiException e) {
            LOGGER.log(Level.WARNING, chatId + ": Error showing list", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Note maker -> note Handler
        // В нем прокидываем сообщение пользователя или кнопке в doNextStep
        // если команда, то в noteHandler, если нет, то в State holder
        try {
            if (update.hasCallbackQuery()) {
                Reminder.userStates.get(Long.toString(update.getCallbackQuery().getMessage().getChatId()))
                        .doNextStep(update.getCallbackQuery().getData());
                LOGGER.info(update.getCallbackQuery().getMessage().getChatId() + ": Received callback query");
            } else if (update.hasMessage()) {
                BotController.parseCommand(update.getMessage().getText(), Long.toString(update.getMessage().getChatId()));
                LOGGER.info(update.getMessage().getChatId() + ": Received message");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error update receiving:", e);
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
