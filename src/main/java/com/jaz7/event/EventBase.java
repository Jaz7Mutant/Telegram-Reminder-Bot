package com.jaz7.event;

import com.jaz7.reminder.UserState;
import com.jaz7.user.User;

public interface EventBase {
    void eventInfo(String chatId, String userMessage);

    void doAction(String chatId, String userMessage);

    UserState parseRespondToInvite(User user, String userMessage);

    UserState parseRespondToDoWish(User user, String userMessage);

    UserState setWish(User user, String userMessage);
}
