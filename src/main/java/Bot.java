import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class Bot {
    private static String botHelp = "This is a bot-reminder." +
            "\r\nFunctions:\r\n\t/help -- show help" +
            "\r\n\t/echo <args> -- print <args>" +
            "\r\n\t/authors -- print authors" +
            "\r\n\t/date -- print current date and time" +
            "\r\n\t/new -- create new note" +
            //"\r\n\t/remove -- remove note" +
            //"\r\n\t/all -- show all your notes" + //TODO
            "\r\n\t/stop -- exit chat bot";
    private static String welcomeText = "Welcome. This is bot-reminder v0.3 alpha";
    private static String authors = "Tolstoukhov Daniil, Gorbunova Sofia, 2019"; //TODO вынести весь текст в json или отдельный класс

    private static UserIO userIO;
    private static Map<String, BiConsumer<String,String>> commands = new HashMap<>();
    private static NoteMaker noteMaker;

    private static String PROXY_HOST = "127.0.0.1" /* proxy host */;
    private static Integer PROXY_PORT = 9150 /* proxy port */;
    private static int notePrinterPeriodInSeconds = 60;

    public static void main(String[] args) {
        //userIO.showMessage(welcomeText, ); // TODO В натройках телеги выстаить
        setUserIO(BotTypes.TELEGRAM_BOT);

        commands.put("/new", noteMaker::addNote);
        commands.put("/remove", noteMaker::removeNote);
        commands.put("/all", noteMaker::showUserNotes);
        commands.put("/stop", Bot::exit);
        commands.put("/help", Bot::help);
        commands.put("/authors", Bot::authors);
        commands.put("/echo", Bot::echo);
        commands.put("/date", Bot::date);

        userIO.listenCommands(commands);
    }

    public static void parseCommand(String command, String chatId) {
        if (!NoteMaker.userStates.containsKey(chatId)) {
            NoteMaker.userStates.put(chatId, new StateHolder(chatId, userIO, noteMaker));
        }
        if (commands.containsKey(command.split(" ")[0])
                && NoteMaker.userStates.get(chatId).currentState == UserStates.IDLE) {
            commands.get(command.split(" ")[0]).accept(command, chatId);
        } else {
            NoteMaker.userStates.get(chatId).doNextStep(command);
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

    private static void setUserIO(BotTypes botType) {
        switch (botType) {
            case CONSOLE_BOT:
                userIO = new ConsoleIO();
                noteMaker = new NoteMaker(userIO, notePrinterPeriodInSeconds);
                userIO.showMessage(welcomeText, null);
                NoteMaker.userStates.put(null,new StateHolder(null, userIO,noteMaker));
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
                noteMaker = new NoteMaker(myBot, notePrinterPeriodInSeconds);
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
