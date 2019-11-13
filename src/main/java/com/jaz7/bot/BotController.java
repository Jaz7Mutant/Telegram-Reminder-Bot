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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class BotController {
    private static String botHelp = "This is a com.jaz7.bot-com.jaz7.reminder." +
            "\r\nFunctions:\r\n\t/help -- show help" +
            "\r\n\t/echo <args> -- print <args>" +
            "\r\n\t/authors -- print authors" +
            "\r\n\t/date -- print current date and time" +
            "\r\n\t/new -- create new note" +
            "\r\n\t/remove -- remove note" +
            "\r\n\t/all -- show all your notes" +
            "\r\n\t/stop -- exit chat com.jaz7.bot";
    private static String welcomeText = "Welcome. This is com.jaz7.bot-com.jaz7.reminder v0.4 alpha \r\n(If you want to write digits as letters use quotes)";
    private static String authors = "Tolstoukhov Daniil, Gorbunova Sofia, 2019"; //TODO вынести весь текст в json или отдельный класс

    private static UserIO userIO;
    private static Map<String, BiConsumer<String,String>> commands = new HashMap<>();
    private static Reminder reminder;

    private static String PROXY_HOST = "127.0.0.1" /* proxy host */;
    private static Integer PROXY_PORT = 9150 /* proxy port */;
    private static int notePrinterPeriodInSeconds = 10;
    private static NoteSerializer noteSerializer = new JsonNoteSerializer();


    private static final Logger LOGGER = Logger.getLogger(BotController.class.getSimpleName());

    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(
                    BotController.class.getResourceAsStream("logging.properties"));
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }

        //userIO.showMessage(welcomeText, ); // TODO В натройках телеги выстаить
        //setUserIO(BotTypes.CONSOLE_BOT);
        setUserIO(BotType.TELEGRAM_BOT);
        LOGGER.info("Bot has been created");
        commands.put("/new", reminder::addNote);
        commands.put("/meeting", reminder::addMeeting);
        commands.put("/join", reminder::joinMeeting);
        commands.put("/remove", reminder::removeNote);
        commands.put("/all", reminder::showUserNotes);
        commands.put("/stop", BotController::exit);
        commands.put("/help", BotController::help);
        commands.put("/authors", BotController::authors);
        commands.put("/echo", BotController::echo);
        commands.put("/date", BotController::date);

        userIO.listenCommands(commands);
    }

    //todo Очередь не нарушать
    public static void parseCommand(String command, String chatId) {
        if (!Reminder.userStates.containsKey(chatId)) {
            LOGGER.info(chatId +": Added new user");
            Reminder.userStates.put(chatId, new NoteKeeper(chatId, userIO, reminder, noteSerializer));
        }
        if (commands.containsKey(command.split(" ")[0])
                && Reminder.userStates.get(chatId).currentState == UserState.IDLE) {
            LOGGER.info(chatId + ": New command from user" + " - " + command);
            commands.get(command.split(" ")[0]).accept(command, chatId);
        } else {
            LOGGER.info(chatId + ": Input command - " + command);
            if (Reminder.userStates.get(chatId).isWorking){
                userIO.showMessage("Bot is busy", chatId);
                return;
            }
            Reminder.userStates.get(chatId).doNextStep(command);
        }
    }

    private static void exit(String command,String chatId) {
        System.exit(0);
    }

    private static void help(String command, String chatId) {
        userIO.showMessage(botHelp, chatId);
    }

    private static void authors(String command, String chatId) {
        userIO.showMessage(authors, chatId);
    }

    private static void date(String command, String chatId) {
        userIO.showMessage(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), chatId);
    }

    private static void echo(String command, String chatId) {
        if (command.length() > 6) {
            userIO.showMessage(command.substring(6), chatId);
        }
    }

    private static void setUserIO(BotType botType) {
        switch (botType) {
            case CONSOLE_BOT:
                userIO = new ConsoleIO();
                reminder = new Reminder(userIO, notePrinterPeriodInSeconds, noteSerializer);
                userIO.showMessage(welcomeText, null);
                Reminder.userStates.put(null,new NoteKeeper(null, userIO, reminder, noteSerializer));
                return;
            case TELEGRAM_BOT:
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
}
