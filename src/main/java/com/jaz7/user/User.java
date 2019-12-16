package com.jaz7.user;

import com.jaz7.bot.BotController;
import com.jaz7.inputOutput.UserIO;
import com.jaz7.reminder.*;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

public class User {
    public NoteKeeper noteKeeper;
    public boolean isWorking;
    public UserState currentState = UserState.IDLE;
    public String chatId;
    public String companionChatId;
    public String wish = new String();
    public Queue<String> bannedUsers = new LinkedList<>();

    private UserIO userIO;
    private static final Logger LOGGER = Logger.getLogger(User.class.getSimpleName());

    public User(UserIO userIO, String chatId, Reminder reminder, NoteSerializer noteSerializer) {
        this.noteKeeper = new NoteKeeper(chatId, userIO, this, reminder, noteSerializer);
        this.userIO = userIO;
        this.chatId = chatId;
        LOGGER.info(String.format("%s: User has been created", chatId));
    }

    public void doNextStep(String userMessage) {
        switch (currentState) {
            case IDLE:
                if (companionChatId != null) {
                    userIO.showMessage(userMessage, companionChatId);
                }
                return;
            case ADDING:
                isWorking = true; // todo ?
                noteKeeper.noteAdder.doNextAddingStep(userMessage);
                isWorking = false; //todo ?
                return;
            case SHOWING:
                LOGGER.info(String.format("%s: Showing notes", chatId));
                currentState = NotePrinter.showUsersNotes(userMessage, chatId, noteKeeper.reminder, currentState);
                return;
            case REMOVING:
                LOGGER.info(String.format("%s: Removing note", chatId));
                currentState = noteKeeper.removeNote(userMessage);
                return;
            case JOINING:
                isWorking = true;
                LOGGER.info(String.format("%s: Joining to meeting", chatId));
                currentState = noteKeeper.joinMeeting(userMessage);
                isWorking = false;
                return;
            case RESPOND_TO_OFFER:
                currentState = noteKeeper.respondToOffer(userMessage);
                return;
            case RESPOND_TO_EVENT_INVITE:
                try {
                    System.out.println("HUIIII");
                    currentState = BotController.currentEvent.parseRespondToInvite(this, userMessage);
                }
                catch (Exception e){
                    throw e;
                }
                return;
            case RESPOND_TO_DO_WISH_OFFER:
                currentState = BotController.currentEvent.parseRespondToDoWish(this, userMessage);
                return;
            case SET_WISH:
                currentState = BotController.currentEvent.setWish(this, userMessage);
                return;
        }
    }
}
