package com.jaz7.bot;

import com.jaz7.chatRoulette.ChatRoulette;
import com.jaz7.event.BotEvent;
import com.jaz7.event.SecretSanta;
import com.jaz7.inputOutput.ConsoleIO;
import com.jaz7.inputOutput.TelegramIO;
import com.jaz7.inputOutput.UserIO;
import com.jaz7.reminder.*;
import com.jaz7.serializer.DataBaseNoteSerializer;
import com.jaz7.serializer.JsonNoteSerializer;
import com.jaz7.serializer.NoteSerializer;
import com.jaz7.user.User;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class BotController {
    public static BotEvent currentEvent;
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
    private static ChatRoulette chatRoulette;
    private static NoteSerializer noteSerializer;
    private static final Logger LOGGER = Logger.getLogger(BotController.class.getSimpleName());

    public static void main(String[] args) {
        // Парсинг настроек логгера
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("src/main/resources/logging.properties"));
            LOGGER.info("Logger settings applied successfully");
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }
        setNoteSerializer(BotOptions.botOptions.get("SerializerType"));
        setUserIO(BotOptions.botOptions.get("BotType"));
        new RespondParser(userIO);
        chatRoulette = new ChatRoulette(userIO);
        currentEvent = new SecretSanta(userIO, reminder,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(3),
                LocalDateTime.now().plusMinutes(3));

        commands.put("/new", reminder::addNote);
        commands.put("/meeting", reminder::addMeeting);
        commands.put("/join", reminder::joinMeeting);
        commands.put("/remove", reminder::removeNote);
        commands.put("/all", reminder::showUserNotes);
        commands.put("/stop", BotController::stop);
        commands.put("/help", BotController::help);
        commands.put("/authors", BotController::authors);
        commands.put("/echo", BotController::echo);
        commands.put("/date", BotController::date);
        commands.put("/chat", chatRoulette::startChatting);
        commands.put("/leave", chatRoulette::stopChatting);
        commands.put("/next", chatRoulette::switchCompanion);
        commands.put("/event", currentEvent::eventInfo);
        commands.put("/wish", currentEvent::doAction);
        LOGGER.info("Command list has been initialized");

        userIO.listenCommands(commands);
    }

    public static void parseCommand(String command, String chatId) {
        // Добавление нового пользователя
        if (!Reminder.users.containsKey(chatId)) {
            Reminder.users.put(chatId, new User(userIO, chatId, reminder, noteSerializer));
            LOGGER.info(chatId + ": Added new user");
        }
        // Исполнение команды
        if (commands.containsKey(command.split(" ")[0])) {
            LOGGER.info(chatId + ": New command from user" + " - " + command);
            commands.get(command.split(" ")[0]).accept(command, chatId);
        } else {
            // Передача аргумента
            LOGGER.info(chatId + ": New message as argument - " + command);
            if (Reminder.users.get(chatId).isWorking) {
                userIO.showMessage("Bot is busy", chatId);
                return;
            }
            Reminder.users.get(chatId).doNextStep(command);
        }
    }

    private static void setUserIO(String botType) {
        LOGGER.info("Setting bot type...");
        switch (botType) {
            case "Console":
                userIO = new ConsoleIO();
                reminder = new Reminder(userIO, notePrinterPeriodInSeconds, noteSerializer);
                userIO.showMessage(welcomeText, null);
                Reminder.users.put(null, new User(userIO, null, reminder, noteSerializer));
                return;
            case "Telegram":
                ApiContextInitializer.init();
                TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
                DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
//                 botOptions.setProxyHost(PROXY_HOST);
//                 botOptions.setProxyPort(PROXY_PORT);
//                 botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
                TelegramIO myBot = new TelegramIO(botOptions);
                userIO = myBot;
                reminder = new Reminder(myBot, notePrinterPeriodInSeconds, noteSerializer);
                try {
                    telegramBotsApi.registerBot(myBot);
                    LOGGER.info("Bot has been registered");

                } catch (TelegramApiException e) {
                    LOGGER.log(Level.SEVERE, "Error in bot registration: ", e.getMessage());
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + botType);
        }
        LOGGER.info("Bot type has been set successfully");
    }

    private static void setNoteSerializer(String serializerType) {
        LOGGER.info("Setting serializer type...");
        switch (serializerType) {
            case "Json":
                noteSerializer = new JsonNoteSerializer();
                break;
            case "DataBase":
                noteSerializer = new DataBaseNoteSerializer();
                try {
                    DataBaseNoteSerializer.connectToDataBase();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Can't connect to database: ", e.getMessage());
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + serializerType);
        }
    }

    private static void stop(String command, String chatId) {
        LOGGER.info(String.format("%s: Removing user", chatId));
        List<Note> userNotes = reminder.getUserNotes(chatId);
        for (Note note : userNotes) {
            reminder.notes.remove(note);
        }
        Reminder.users.remove(chatId);
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
        String[] words = command.split(" ");
        if (words.length > 1)
            userIO.showMessage(String.join(" ", Arrays.copyOfRange(words, 1, words.length)), chatId);
        else
            userIO.showMessage("Эй, что за пустота?", chatId);
    }
}
