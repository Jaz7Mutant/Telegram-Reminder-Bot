package com.jaz7.event;

import com.jaz7.user.UserState;
import com.jaz7.user.User;

public interface BotEvent {
    void eventInfo(String chatId, String userMessage);

    void doAction(String chatId, String userMessage);

    UserState respondToInvite(User user, String userMessage);

    UserState respondToDoWish(User user, String userMessage);

    UserState setWish(User user, String userMessage);
}
