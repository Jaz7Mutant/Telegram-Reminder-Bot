package bot;

import inputOutput.ConsoleIO;
import inputOutput.TelegramIO;
import inputOutput.UserIO;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reminder.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class BotController {
    private static BotOptions botOptions = new BotOptions();
    private static String botHelp = BotOptions.botAnswers.get("BotHelp");
    private static String welcomeText = BotOptions.botAnswers.get("WelcomeText");
    private static String authors = BotOptions.botAnswers.get("Authors");

    private static UserIO userIO;
    private static Map<String, BiConsumer<String,String>> commands = new HashMap<>();
    private static Reminder reminder;

    private static String PROXY_HOST = BotOptions.botOptions.get("ProxyHost") /* proxy host */;
    private static Integer PROXY_PORT = Integer.parseInt(BotOptions.botOptions.get("ProxyPort")) /* proxy port */;
    private static int notePrinterPeriodInSeconds = Integer.parseInt(BotOptions.botOptions.get("NotePrinterPeriod"));
    private static NoteSerializer noteSerializer = new JsonNoteSerializer();

    public static void main(String[] args) {
        //userIO.showMessage(welcomeText, ); // TODO В натройках телеги выстаить
        //setUserIO(BotTypes.CONSOLE_BOT);
        setUserIO(BotOptions.botOptions.get("BotType"));

        commands.put("/new", reminder::addNote);
        commands.put("/remove", reminder::removeNote);
        commands.put("/all", reminder::showUserNotes);
        commands.put("/stop", BotController::exit);
        commands.put("/help", BotController::help);
        commands.put("/authors", BotController::authors);
        commands.put("/echo", BotController::echo);
        commands.put("/date", BotController::date);

        userIO.listenCommands(commands);
    }

    public static void parseCommand(String command, String chatId) {
        if (!Reminder.userStates.containsKey(chatId)) {
            Reminder.userStates.put(chatId, new NoteKeeper(chatId, userIO, reminder, noteSerializer));
        }
        if (commands.containsKey(command.split(" ")[0])
                && Reminder.userStates.get(chatId).currentState == UserState.IDLE) {
            commands.get(command.split(" ")[0]).accept(command, chatId);
        } else {
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
        userIO.showMessage(LocalDateTime.now().format(DateTimeFormatter.ofPattern(
                BotOptions.botOptions.get("DateTimePattern"))), chatId);
    }

    private static void echo(String command, String chatId) {
        if (command.length() > 6) {
            userIO.showMessage(command.substring(6), chatId);
        }
    }

    private static void setUserIO(String botType) {
        switch (botType) {
            case "Console":
                userIO = new ConsoleIO();
                reminder = new Reminder(userIO, notePrinterPeriodInSeconds, noteSerializer);
                userIO.showMessage(welcomeText, null);
                Reminder.userStates.put(null,new NoteKeeper(null, userIO, reminder, noteSerializer));
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
}
