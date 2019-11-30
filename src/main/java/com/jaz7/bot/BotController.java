package com.jaz7.bot;

import com.jaz7.inputOutput.ConsoleIO;
import com.jaz7.inputOutput.TelegramIO;
import com.jaz7.inputOutput.UserIO;
import com.jaz7.reminder.*;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class BotController {
    private static BotOptions botOptions = new BotOptions(); // Инициализация всех параметров
    private static String botHelp = BotOptions.botAnswers.get("BotHelp");
    private static String welcomeText = BotOptions.botAnswers.get("WelcomeText");
    private static String authors = BotOptions.botAnswers.get("Authors");
    private static int notePrinterPeriodInSeconds = Integer.parseInt(BotOptions.botOptions.get("NotePrinterPeriod"));
    private static String PROXY_HOST = BotOptions.botOptions.get("ProxyHost");
    private static Integer PROXY_PORT = Integer.parseInt(BotOptions.botOptions.get("ProxyPort"));

    private static UserIO userIO;
    private static Map<String, BiConsumer<String, String>> commands = new HashMap<>();
    private static Reminder reminder;
    private static NoteSerializer noteSerializer = new JsonNoteSerializer();
    private static final Logger LOGGER = Logger.getLogger(BotController.class.getSimpleName());

    public static void main(String[] args) {
        // Парсинг настроек логгера
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("src/main/resources/logging.properties"));
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }
        setUserIO(BotOptions.botOptions.get("BotType"));
        new RespondParser(userIO);

        commands.put("/new", reminder::addNote);
        commands.put("/meeting", reminder::addMeeting);
        commands.put("/join", reminder::joinMeeting);
        commands.put("/remove", reminder::removeNote);
        commands.put("/all", reminder::showUserNotes);
        //commands.put("/stop", BotController::stop);
        commands.put("/help", BotController::help);
        commands.put("/authors", BotController::authors);
        commands.put("/echo", BotController::echo);
        commands.put("/date", BotController::date);
        commands.put("/chat", reminder::startChatting);
        commands.put("/leave", reminder::stopChatting);
        commands.put("/next", reminder::switchCompanion);

        userIO.listenCommands(commands);
    }

    public static void parseCommand(String command, String chatId) {
        // Добавление нового пользователя
        if (!Reminder.userStates.containsKey(chatId)) {
            LOGGER.info(chatId + ": Added new user");
            Reminder.userStates.put(chatId, new NoteKeeper(chatId, userIO, reminder, noteSerializer));
        }
        // Исполнение команды
        if (commands.containsKey(command.split(" ")[0])
                && Reminder.userStates.get(chatId).currentState == UserState.IDLE) {
            LOGGER.info(chatId + ": New command from user" + " - " + command);
            commands.get(command.split(" ")[0]).accept(command, chatId);
        } else {
            // Передача аргумента
            LOGGER.info(chatId + ": Input command - " + command);
            if (Reminder.userStates.get(chatId).isWorking) {
                userIO.showMessage("Bot is busy", chatId);
                return;
            }
            Reminder.userStates.get(chatId).doNextStep(command);
        }
    }

    private static void setUserIO(String botType) {
        switch (botType) {
            case "Console":
                userIO = new ConsoleIO();
                reminder = new Reminder(userIO, notePrinterPeriodInSeconds, noteSerializer);
                userIO.showMessage(welcomeText, null);
                Reminder.userStates.put(null, new NoteKeeper(null, userIO, reminder, noteSerializer));
                return;
            case "Telegram":
                ApiContextInitializer.init();
                TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
                DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
                botOptions.setProxyHost(PROXY_HOST);
                botOptions.setProxyPort(PROXY_PORT);
                // Select proxy type: [HTTP|SOCKS4|SOCKS5] (default: NO_PROXY)
                botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
                TelegramIO myBot = new TelegramIO(botOptions);
                userIO = myBot;
                reminder = new Reminder(myBot, notePrinterPeriodInSeconds, noteSerializer);
                try {
                    telegramBotsApi.registerBot(myBot);
                    System.out.println("Bot registered");

                } catch (TelegramApiException e) {
                    System.out.println("Error is here");
                    e.printStackTrace();
                }
        }
    }

    private static void stop(String command, String chatId) {
        System.exit(0);
    }

    private static void help(String command, String chatId) {
        userIO.showMessage(botHelp, chatId);
    }

    private static void authors(String command, String chatId) {
        userIO.showMessage(authors, chatId);
    }

    private static void date(String command, String chatId) {
        userIO.showMessage(LocalDateTime.now().format(DateTimeFormatter.ofPattern(
                BotOptions.botOptions.get("DateTimePattern"))), chatId);
    }

    private static void echo(String command, String chatId) {
        if (command.length() > 6) {
            userIO.showMessage(command.substring(6), chatId);
        }
    }
}
