package com.jaz7.inputOutput;

import java.util.Map;
import java.util.function.BiConsumer;

public interface UserIO {

    void listenCommands(Map<String, BiConsumer<String, String>> commands);

    void showMessage(String message, String chatId);

    String getUserText(String prompt, String chatId);

    void showOnClickButton(String header, String[] buttons, String chatId);

    void showList(String prompt, String[] elements, String chatId);
}
