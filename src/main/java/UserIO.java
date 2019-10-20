import java.util.Map;
import java.util.function.BiConsumer;

public interface UserIO {

    public void listenCommands(Map<String, BiConsumer<String,String>> commands);

    public void showMessage(String message, String chatId);

    public String getUserText(String prompt, String chatId);

    public void showOnClickButton(String header, String[] buttons, String chatId);

    public void showList(String prompt, String[] elements, String chatId);
}
