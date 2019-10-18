import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramBot extends TelegramLongPollingBot {
    private static final String botUserName = "SimpleAutoReminderBot";
    private static final String botToken = "932793430:AAEe098f_fG7JYPrBupkqaxKRqcarQvUNKo";

    //protected TelegramBot(DefaultBotOptions botOptions) {
    //    super(botOptions);
    //}


//        // Select proxy type: [HTTP|SOCKS4|SOCKS5] (default: NO_PROXY);
//        botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
//        BotApi myBot = new BotApi(botOptions);
//        try {
//            telegramBotsApi.registerBot(myBot);
//            System.out.println("Bot registered");
//        } catch (TelegramApiException e) {
//            System.out.println("Error is here");
//            e.printStackTrace();
//        }
 //   }


//    @Override
    public void onUpdateReceived(Update update) {
        //todo
        // Note maker -> note Handler
        // В нем прокидываем сообщение пользователя или кнопке в doNextStep
        // если команда, то в noteHandler, если нет, то в State holder
        if(update.hasCallbackQuery()){
            NoteMaker.userStates.get(update.getCallbackQuery().getMessage().getChatId())
                    .doNextStep(update.getCallbackQuery().getData());
        }
        else if (update.hasMessage()){
            Bot.parseCommand(update.getMessage().getText(),update.getMessage().getChatId());
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
