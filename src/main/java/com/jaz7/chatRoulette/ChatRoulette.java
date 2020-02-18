package com.jaz7.chatRoulette;

import com.jaz7.bot.BotOptions;
import com.jaz7.inputOutput.UserIO;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import static com.jaz7.reminder.Reminder.users;

public class ChatRoulette {
    public static final List<String> readyToChatUsers = new CopyOnWriteArrayList<>(); //Пользователи, которые ищут собеседника
    public static List<String> chattingUsers = new ArrayList<>(); //Пользователи, которые находятся в состоянии общения

    private static final Logger LOGGER = Logger.getLogger(ChatRoulette.class.getSimpleName());
    private UserIO userIO;

    public ChatRoulette(UserIO userIO) {
        LOGGER.info("Initializing ChatRoulette");
        this.userIO = userIO;
    }

    public void startChatting(String command, String chatId) {
        LOGGER.info(String.format("%s: Switch to chatting state", chatId));
        userIO.showMessage(BotOptions.botAnswers.get("CurrentOnline") + readyToChatUsers.size(), chatId);
        if (!chattingUsers.contains(chatId)) {
            chattingUsers.add(chatId);
            readyToChatUsers.add(chatId);
            switchCompanion(null, chatId);
        }
    }

    public void switchCompanion(String command, String chatId) {
        LOGGER.info(String.format("%s: Finding new companion", chatId));
        if (!chattingUsers.contains(chatId)) {
            LOGGER.info(String.format("%s: User is not in chatting state", chatId));
            userIO.showMessage(BotOptions.botAnswers.get("JoinChatFirst"), chatId);
            return;
        }
        String currentCompanion = users.get(chatId).companionChatId;
        if (currentCompanion != null) {
            Queue<String> bannedUsers = users.get(chatId).bannedUsers;
            bannedUsers.add(currentCompanion);
            if (bannedUsers.size() > 3) {
                bannedUsers.poll();
            }
            if (users.get(currentCompanion).companionChatId != null) {
                userIO.showMessage(BotOptions.botAnswers.get("CompanionLeft"), currentCompanion);
            }
            users.get(currentCompanion).companionChatId = null;
            users.get(chatId).companionChatId = null;
            readyToChatUsers.add(currentCompanion);
            readyToChatUsers.add(chatId);
            switchCompanion(null, currentCompanion);
        }
        userIO.showMessage(BotOptions.botAnswers.get("LookingForCompanion"), chatId);
        String companion = getRandomUserToChat(chatId);
        if (companion.equals("")) {
            userIO.showMessage(BotOptions.botAnswers.get("NoOnlineUsers"), chatId);
            LOGGER.info(String.format("%s: No free users to chat", chatId));
            return;
        }
        users.get(chatId).companionChatId = companion;
        users.get(companion).companionChatId = chatId;
        userIO.showMessage(BotOptions.botAnswers.get("ChattingWith") + chatId.substring(2,6), companion);
        userIO.showMessage(BotOptions.botAnswers.get("ChattingWith") + chatId.substring(2,6), chatId);
        readyToChatUsers.remove(chatId);
        readyToChatUsers.remove(companion);
        LOGGER.info(String.format("%s: Connection completed with %s", chatId, companion));
        LOGGER.info(String.format("%s: Connection completed %s", companion, chatId));
    }

    public void stopChatting(String command, String chatId) {
        LOGGER.info(String.format("%s: Switching off chatting", chatId));
        if (!chattingUsers.contains(chatId)){
            return;
        }
        chattingUsers.remove(chatId);
        readyToChatUsers.remove(chatId);
        String currentCompanion = users.get(chatId).companionChatId;
        if (currentCompanion != null && users.get(currentCompanion).companionChatId.equals(chatId)) {
            users.get(chatId).companionChatId = null;
            userIO.showMessage(BotOptions.botAnswers.get("CompanionLeft"), currentCompanion);
            users.get(currentCompanion).companionChatId = null;
            readyToChatUsers.add(currentCompanion);
            switchCompanion(null, currentCompanion);
        }
        userIO.showMessage(BotOptions.botAnswers.get("LeftTheChat"), chatId);
    }

    private String getRandomUserToChat(String finderChatId) {
        String user;
        int i = 0;
        do {
            LOGGER.info(String.format("%s: Trying to find companion...", finderChatId));
            if (i >= readyToChatUsers.size()) {
                return "";
            }
            user = readyToChatUsers.get(i);
            i++;
        }
        while (user.equals(finderChatId) || users.get(finderChatId).bannedUsers.contains(user)
                || users.get(user).bannedUsers.contains(finderChatId));
        return user;
    }
}
