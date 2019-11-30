package com.jaz7.reminder;

import com.jaz7.bot.BotOptions;
import com.jaz7.inputOutput.UserIO;

import java.util.List;
import java.util.logging.Logger;

public class NoteKeeper {
    public UserState currentState = UserState.IDLE;
    public String companionChatId;
//    public List<String> bannedUsers; TODO
    public boolean isChatting;
    public boolean isWorking = false;
    public List<Note> userNotes;
    public NoteAdder noteAdder;
    private UserIO userIO;
    private String chatId;
    private Reminder reminder;
    private NoteSerializer noteSerializer;
    private static final Logger LOGGER = Logger.getLogger(NoteKeeper.class.getSimpleName());

    public NoteKeeper(String chatId, UserIO userIO, Reminder reminder, NoteSerializer noteSerializer) {
        this.reminder = reminder;
        this.chatId = chatId;
        this.userIO = userIO;
        this.noteSerializer = noteSerializer;
        this.noteAdder = new NoteAdder(userIO, noteSerializer, reminder, chatId, this);
        LOGGER.info(chatId + ": NoteKeeper has been created");
    }

    public void doNextStep(String userMessage) {
        switch (currentState) {
            case IDLE:
                if (companionChatId != null){
                    userIO.showMessage(userMessage, companionChatId);
                }
                return;
            case ADDING:
                isWorking = true; // todo ?
                noteAdder.doNextAddingStep(userMessage);
                isWorking = false; //todo ?
                return;
            case SHOWING:
                LOGGER.info(chatId + ": Showing notes");
                currentState = NotePrinter.showUsersNotes(userMessage, chatId, reminder, currentState);
                return;
            case REMOVING:
                LOGGER.info(chatId + ": Removing note");
                removeNote(userMessage);
                return;
            case JOINING:
                isWorking = true;
                LOGGER.info(chatId + ": Joining to meeting");
                currentState = joinMeeting(userMessage);
                isWorking = false;
        }
    }

    private UserState joinMeeting(String userMessage){
        synchronized (reminder.notes) {
            for (Note currNote : reminder.notes){
                if (currNote.getToken() == null){
                    continue;
                }
                if (currNote.getToken().equals(userMessage)){
                    reminder.notes.add(currNote.copy(chatId));
                    reminder.notePrinter.run();
                    int stringLimit = 20;
                    if (currNote.getText().length() < 20) {
                        stringLimit = currNote.getText().length();
                    }
                    userIO.showMessage(BotOptions.botAnswers.get("NewNote")
                            + currNote.getText().substring(0, stringLimit)
                            + BotOptions.botAnswers.get("WithRemind")
                            + currNote.getRemindDate().format(NotePrinter.dateTimeFormatter), chatId);
                    noteSerializer.serializeNotes(reminder.notes);
                    LOGGER.info(chatId + ": Joined meeting");
                    return UserState.IDLE;
                }
            }
            userIO.showMessage("Wrong token", chatId);
            return UserState.IDLE;
        }
    }

    private synchronized void removeNote(String userMessage){ // TODO Если владелец удаляет заметку, удалять все заметки с таким же токеном, без слова MEET в начале
        LOGGER.info(chatId + ": Removing note...");
        userNotes = NotePrinter.getUserNotes(reminder, chatId);
        int respond;
        try {
            respond = RespondParser.parseRemoveNoteRespond(userMessage, userNotes, chatId);
        }
        catch (IllegalArgumentException e){
            currentState = UserState.IDLE;
            userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), chatId);
            return;
        }
        synchronized (reminder.notes) {
            reminder.notes.remove(userNotes.get(respond - 1));
            currentState = UserState.IDLE;
            userIO.showMessage(BotOptions.botAnswers.get("Removed"), chatId);
            noteSerializer.serializeNotes(reminder.notes); // TODO Можно убрать
        }
        LOGGER.info(chatId + ": Note has been removed");
    }
}