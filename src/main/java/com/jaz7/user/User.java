package com.jaz7.user;

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
    public Queue<String> bannedUsers = new LinkedList<>();

    private UserIO userIO;
    private static final Logger LOGGER = Logger.getLogger(User.class.getSimpleName());

    public User(UserIO userIO, String chatId, Reminder reminder, NoteSerializer noteSerializer){
        this.noteKeeper = new NoteKeeper(chatId, userIO, this, reminder, noteSerializer);
        this.userIO = userIO;
        this.chatId = chatId;
        LOGGER.info(chatId + ": User has been created");
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
                LOGGER.info(chatId + ": Showing notes");
                currentState = NotePrinter.showUsersNotes(userMessage, chatId, noteKeeper.reminder, currentState);
                return;
            case REMOVING:
                LOGGER.info(chatId + ": Removing note");
                currentState = noteKeeper.removeNote(userMessage);
                return;
            case JOINING:
                isWorking = true;
                LOGGER.info(chatId + ": Joining to meeting");
                currentState = noteKeeper.joinMeeting(userMessage);
                isWorking = false;
                return;
            case RESPOND_TO_OFFER:
                currentState = noteKeeper.respondToOffer(userMessage);
                return;
        }
    }
}
