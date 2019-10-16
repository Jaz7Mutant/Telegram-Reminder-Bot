import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class TelegramBot extends TelegramLongPollingBot {
    private static final String botUserName = "Simple Reminder Bot";
    private static final String botToken = "932793430:AAEe098f_fG7JYPrBupkqaxKRqcarQvUNKo";



    @Override
    public void onUpdateReceived(Update update) {
        //todo
        // Note maker -> note Handler
        // В нем прокидываем сообщение пользователя или кнопке в doNextStep

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
