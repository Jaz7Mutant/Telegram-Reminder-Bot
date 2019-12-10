package com.jaz7.inputOutput;

import com.jaz7.bot.BotController;
import com.jaz7.reminder.Reminder;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
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
    public void listenCommands(Map<String, BiConsumer<String, String>> commands) {
    }

    @Override
    public void showMessage(String message, String chatId) {
        try {
            execute(new SendMessage()
                    .setChatId(chatId)
                    .setText(message));
            LOGGER.info(String.format("%s: Message has been send", chatId));
        } catch (TelegramApiException e) {
            LOGGER.log(
                    Level.WARNING,
                    String.format(
                            "%s: Error sending message - %s ; %s",
                            chatId,
                            message.replace('\n', ' '),
                            e.getMessage()),
                    e);
        }
    }

    @Override
    public String getUserText(String prompt, String chatId) {
        return null;
    }

    @Override
    public void showOnClickButton(String header, String[] buttons, String chatId) {
        System.out.println(Arrays.toString(buttons));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList;
        if (header.contains("day")) {
            rowList = createKeyboard(7, buttons);
        } else {
            rowList = createKeyboard(3, buttons);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);
        try {
            execute(new SendMessage().setText(header).setChatId(chatId).setReplyMarkup(inlineKeyboardMarkup));
            LOGGER.info(String.format("%s: Buttons shown successfully - %s", chatId, header));
        } catch (TelegramApiException e) {
            LOGGER.log(
                    Level.WARNING,
                    String.format("%s: Error showing buttons - %s ; %s", chatId, header, e.getMessage()),
                    e);
        }
    }

    private List<List<InlineKeyboardButton>> createKeyboard(int buttonsInRow, String[] buttons) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
        for (String button : buttons) {
            buttonsRow.add(new InlineKeyboardButton()
                    .setText(button)
                    .setCallbackData(button));
            if (buttonsRow.size() == buttonsInRow) {
                rowList.add(buttonsRow);
                buttonsRow = new ArrayList<>();
            }
        }
        if (buttonsRow.size() != 0) {
            rowList.add(buttonsRow);
        }
        return rowList;
    }

    @Override
    public void showList(String prompt, String[] elements, String chatId) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elements.length; i++) {
            sb.append(i + 1);
            sb.append(". ");
            sb.append(elements[i]);
            sb.append("\r\n");
        }
        String text = sb.toString();
        if (elements.length == 0) {
            text = "No elements";
        }
        try {
            execute(new SendMessage().setText(prompt).setChatId(chatId));
            execute(new SendMessage().setText(text).setChatId(chatId));
            LOGGER.info(String.format("%s: List shown successfully - %s", chatId, prompt));
        } catch (TelegramApiException e) {
            LOGGER.log(
                    Level.WARNING,
                    String.format("%s: Error showing list - %s ; %s", chatId, prompt, e.getMessage()),
                    e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Получение очередного сообщения от пользователя

        // Передача аргумента из кнопки
        if (update.hasCallbackQuery()) {
            Reminder.users.get(Long.toString(update.getCallbackQuery().getMessage().getChatId()))
                    .doNextStep(update.getCallbackQuery().getData());
            LOGGER.info(
                    String.format("%s: Received callback query", update.getCallbackQuery().getMessage().getChatId()));
            // Парсинг сообщения
        } else if (update.hasMessage()) {
            BotController.parseCommand(
                    update.getMessage().getText(), Long.toString(update.getMessage().getChatId()));
            LOGGER.info(
                    String.format(
                            "%s: Received message - %s",
                            update.getMessage().getChatId(),
                            update.getMessage().getText()));
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
