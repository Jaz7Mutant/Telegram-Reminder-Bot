package com.jaz7.chatRoulette;

import com.jaz7.bot.BotOptions;
import com.jaz7.inputOutput.UserIO;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

import static com.jaz7.reminder.Reminder.users;

public class ChatRoulette {
    public static final List<String> readyToChatUsers = new ArrayList<>(); //Пользователи, которые ищут собеседника
    public static List<String> chattingUsers =  new ArrayList<>(); //Пользователи, которые находятся в состоянии общения

    private static final Logger LOGGER = Logger.getLogger(ChatRoulette.class.getSimpleName());
    private UserIO userIO;

    public ChatRoulette(UserIO userIO){
        LOGGER.info("Initializing ChatRoulette");
        this.userIO = userIO;
    }

    public void startChatting(String command, String chatId){
        synchronized (readyToChatUsers) {
            LOGGER.info(chatId + ": Switch to chatting state");
            userIO.showMessage(BotOptions.botAnswers.get("CurrentOnline") + readyToChatUsers.size(), chatId);
            chattingUsers.add(chatId);
            readyToChatUsers.add(chatId);
            switchCompanion(null, chatId);
        }
    }

    public void switchCompanion(String command, String chatId){
        LOGGER.info(chatId + ": Finding new companion");
        synchronized (users) {
            synchronized (readyToChatUsers) {
                if (!readyToChatUsers.contains(chatId)) {
                    readyToChatUsers.add(chatId);
                }
                String currentCompanion = users.get(chatId).companionChatId;
                if (currentCompanion != null) {
                    Queue<String> bannedUsers = users.get(chatId).bannedUsers;
                    bannedUsers.add(currentCompanion);
                    if (bannedUsers.size() > 3){
                        bannedUsers.poll();
                    }
                    if (users.get(currentCompanion).companionChatId != null){
                        userIO.showMessage(BotOptions.botAnswers.get("CompanionLeft"), currentCompanion);
                        userIO.showMessage(BotOptions.botAnswers.get("LookingForCompanion"), currentCompanion);
                    }
                    users.get(currentCompanion).companionChatId = null;
                    readyToChatUsers.add(currentCompanion);
                }
                users.get(chatId).companionChatId = null;
                userIO.showMessage(BotOptions.botAnswers.get("LookingForCompanion"), chatId);
                if (readyToChatUsers.size() == 1) {
                    userIO.showMessage(BotOptions.botAnswers.get("NoOnlineUsers"), chatId);
                    LOGGER.info(chatId + ": No free users to chat");
                } else {
                    String companion = getRandomUserToChat(chatId);
                    if (companion.equals("")){
                        userIO.showMessage(BotOptions.botAnswers.get("NoOnlineUsers"), chatId);
                        LOGGER.info(chatId + ": No free users to chat");
                        return;
                    }
                    users.get(chatId).companionChatId = companion;
                    users.get(companion).companionChatId = chatId;
                    userIO.showMessage(BotOptions.botAnswers.get("ChattingWith") + chatId, companion);
                    userIO.showMessage(BotOptions.botAnswers.get("ChattingWith") + companion, chatId);
                    readyToChatUsers.remove(chatId);
                    readyToChatUsers.remove(companion);
                    LOGGER.info(chatId + ": Connection completed with " + companion);
                    LOGGER.info(companion + ": Connection completed " + chatId);
                }
            }
        }
    }

    public void stopChatting(String command, String chatId){
        synchronized (readyToChatUsers) {
            LOGGER.info(chatId + ": Switching off chatting");
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
    }

    private String getRandomUserToChat(String finderChatId){
        String user = "";
        int i = 0;
        while (user.equals("") || user.equals(finderChatId) || users.get(finderChatId).bannedUsers.contains(user)){
            LOGGER.info(finderChatId + ": Trying to find companion...");
            if (i >= readyToChatUsers.size()){
                return "";
            }
            user = readyToChatUsers.get(i);
            i++;
        }
        return user;
    }
}
