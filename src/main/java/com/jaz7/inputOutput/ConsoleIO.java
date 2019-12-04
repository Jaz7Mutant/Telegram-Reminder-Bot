package com.jaz7.inputOutput;

import com.jaz7.bot.BotController;

import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;

public class ConsoleIO implements UserIO {

    @Override
    public void listenCommands(Map<String, BiConsumer<String, String>> commands) {
        String currentCommand = "";
        while (!currentCommand.equals("/exit")) {
            currentCommand = getUserText(null, "");
            BotController.parseCommand(currentCommand, "");
        }
    }

    @Override
    public void showMessage(String message, String chatId) {
        System.out.println(message);
    }

    @Override
    public String getUserText(String prompt, String chatId) {
        if (prompt != null) {
            System.out.println(prompt);
        }
        Scanner in = new Scanner(System.in);
        String userInput = in.nextLine();
        try {
            return Integer.toString(Integer.parseInt(userInput) - 1);
        } catch (NumberFormatException e) {
            return userInput;
        }
    }

    @Override
    public void showOnClickButton(String header, String[] buttons, String chatId) {
        System.out.println(header);
        for (int i = 0; i < buttons.length; i++) {
            System.out.println((i + 1) + ". " + buttons[i]);
        }
    }

    @Override
    public void showList(String prompt, String[] elements, String chatId) {
        System.out.println(prompt);
        for (int i = 0; i < elements.length; i++) {
            String str = elements[i];
            System.out.println((i + 1) + ". " + str);
        }
    }
}
