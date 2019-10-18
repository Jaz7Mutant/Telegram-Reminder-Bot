import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramIO implements UserIO {
    private final TelegramLongPollingBot bot;

    public TelegramIO(TelegramLongPollingBot bot){
        this.bot = bot;
    }

    @Override
    public void showMessage(String message, String chatId) {
        try {
            bot.execute(new SendMessage()
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
    public int getOnClickButton(String[] buttons, String chatId) {
        return 0;
    }

    @Override
    public void showList(String prompt, String[] elements, String chatId) {

    }
}
