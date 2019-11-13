package com.jaz7.reminder;

import com.jaz7.inputOutput.UserIO;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

import static com.jaz7.reminder.AddingState.*;

public class NoteKeeper {
    public UserState currentState = UserState.IDLE;
    public AddingState addingState;
    public boolean isWorking = false;
    private UserIO userIO;
    private String chatId;
    private Reminder reminder;

    private Calendar newRawDate = new GregorianCalendar();
    private Calendar newRawRemindDate = new GregorianCalendar();
    private boolean isMeeting = false;
    private String newNoteText = null;
    private LocalDateTime newNoteDate = null;
    private LocalDateTime newNoteRemindDate = null;
    private NoteSerializer noteSerializer;
    private static final Logger LOGGER = Logger.getLogger(NoteKeeper.class.getSimpleName());

    public NoteKeeper(String chatId, UserIO userIO, Reminder reminder, NoteSerializer noteSerializer) {
        this.reminder = reminder;
        this.chatId = chatId;
        this.userIO = userIO;
        this.noteSerializer = noteSerializer;
        LOGGER.info(chatId + ": NoteKeeper has been created");
    }

    public void doNextStep(String userMessage) {
        switch (currentState) {
            case IDLE:
                return;
            case ADDING:
                doNextAddingStep(userMessage);
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

    private synchronized UserState joinMeeting(String userMessage){
        synchronized (reminder.notes) {
            Note meeting;
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
                    userIO.showMessage("You have a new note \""
                            + currNote.getText().substring(0, stringLimit) + "...\" with remind on " + currNote.getRemindDate().format(NotePrinter.dateTimeFormatter), chatId);
                    noteSerializer.serializeNotes(reminder.notes);
                    LOGGER.info(chatId + ": Joined meeting");
                    return UserState.IDLE;
                }
            }
            userIO.showMessage("Wrong token", chatId);
            return UserState.IDLE;
        }
    }

    private void doNextAddingStep(String userMessage) {
        switch (addingState) {
            case SET_MEETING:
                isMeeting = true; // Продолжить добавление заметки, поэтому нет break
            case SET_TEXT:
                LOGGER.info(chatId + ": Setting text");
                if (userMessage == null) {
                    userIO.showMessage("Wrong format", chatId);
                    userIO.showMessage("Write your note", chatId);
                    return;
                } else {
                    newNoteText = userMessage;
                    LOGGER.info(chatId + ": Text has been set");
                    userIO.showMessage("When will it happen?", chatId);
                    userIO.showOnClickButton("Choose year", DateTimeParser.years, chatId);
                    addingState = SET_YEAR;
                    return;
                }
            case SET_YEAR:
                LOGGER.info(chatId + ": Setting year");
                addingState = DateTimeParser.setYear(newRawDate, userMessage, chatId, addingState);
                return;
            case SET_REMIND_YEAR:
                LOGGER.info(chatId + ": Setting remind year");
                addingState = DateTimeParser.setYear(newRawRemindDate, userMessage, chatId, addingState);
                return;
            case SET_MONTH:
                LOGGER.info(chatId + ": Setting month");
                addingState = DateTimeParser.setMonth(newRawDate, userMessage, chatId, addingState);
                return;
            case SET_REMIND_MONTH:
                LOGGER.info(chatId + ": Setting remind month");
                addingState = DateTimeParser.setMonth(newRawRemindDate, userMessage, chatId, addingState);
                return;
            case SET_DAY:
                LOGGER.info(chatId + ": Setting day");
                addingState = DateTimeParser.setDay(newRawDate,userMessage, chatId, addingState);
                return;
            case SET_REMIND_DAY:
                LOGGER.info(chatId + ": Setting remind day");
                addingState = DateTimeParser.setDay(newRawRemindDate,userMessage, chatId, addingState);
                return;
            case SET_TIME:
                LOGGER.info(chatId + ": Setting time");
                addingState = DateTimeParser.setTime(newRawDate,userMessage,chatId,addingState);
                LOGGER.info(chatId + ": Setting remind type");
                if (addingState == SET_REMIND){
                    newNoteDate = LocalDateTime.ofInstant(newRawDate.toInstant(), newRawDate.getTimeZone().toZoneId());
                    userIO.showOnClickButton("Set the date of remind", new String[]{
                            "No remind",
                            "A hour before",
                            "A day before",
                            "A week before",
                            "Set date..."}, chatId);
                }else if (addingState == AddingState.IDLE){
                    finishAddNote();
                }
                return;
            case SET_REMIND_TIME:
                LOGGER.info(chatId + ": Setting remind time");
                addingState = DateTimeParser.setTime(newRawRemindDate,userMessage,chatId,addingState);
                if (addingState == IDLE) {
                    finishAddNote();
                }
                return;
            case SET_REMIND:
                LOGGER.info(chatId + ": Setting remind by offset");
                setRemind(userMessage);
        }
    }

    private void setRemind(String userMessage){
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
            if (respond > 4){
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e){
            userIO.showMessage("Wrong format", chatId);
            return;
        }
        newNoteDate = LocalDateTime.ofInstant(newRawDate.toInstant(), newRawDate.getTimeZone().toZoneId());
        switch (respond) {
            case 0:
                newNoteRemindDate = newNoteDate;
                break;
            case 1:
                newNoteRemindDate = newNoteDate.minusHours(1);
                break;
            case 2:
                newNoteRemindDate = newNoteDate.minusDays(1);
                break;
            case 3:
                newNoteRemindDate = newNoteDate.minusWeeks(1);
                break;
            case 4:
                userIO.showOnClickButton("Choose year", DateTimeParser.years, chatId);
                addingState = SET_REMIND_YEAR;
                return;
            default:
                throw new IllegalArgumentException();
        }
        finishAddNote();
    }

    //todo lock
    private void finishAddNote(){
        LOGGER.info(chatId + ": Adding new note...");
        synchronized (reminder.notes) {
            Note newNote = new Note(chatId, newNoteText, newNoteDate, newNoteRemindDate);
            if (isMeeting) {
                LOGGER.info(chatId + ": Setting meeting token");
                newNote.setToken();
            }
            reminder.notes.add(newNote);
            reminder.notePrinter.run();
            addingState = AddingState.IDLE;
            currentState = UserState.IDLE;
            int stringLimit = 20;
            if (newNoteText.length() < 20) {
                stringLimit = newNoteText.length();
            }
            if (isMeeting){
                userIO.showMessage("You have a new meeting \""
                        + newNoteText.substring(0, stringLimit) + "...\" with remind on " + newNoteRemindDate.format(NotePrinter.dateTimeFormatter), chatId);
                userIO.showMessage("The token of your meeting: ", chatId);
                userIO.showMessage(newNote.getToken(), chatId);
                userIO.showMessage("You can share it with your friends", chatId);
            }
            else {
                userIO.showMessage("You have a new note \""
                        + newNoteText.substring(0, stringLimit) + "...\" with remind on " + newNoteRemindDate.format(NotePrinter.dateTimeFormatter), chatId);
                noteSerializer.serializeNotes(reminder.notes);
            }
        }
        LOGGER.info(chatId + ": New note has been added");
        isMeeting = false;
    }

    //todo lock
    private synchronized void removeNote(String userMessage){
        LOGGER.info(chatId + ": Removing note...");
        int respond;
        List<Note> userNotes = NotePrinter.getUserNotes(reminder, chatId);
        try {
            respond = Integer.parseInt(userMessage);
            if (respond > userNotes.size() || respond <= 0){
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            userIO.showMessage("Wrong format", chatId);
            currentState = UserState.IDLE;
            return;
        }
        synchronized (reminder.notes) {
            reminder.notes.remove(userNotes.get(respond - 1));
            currentState = UserState.IDLE;
            userIO.showMessage("Note has been removed", chatId);
        }
        LOGGER.info(chatId + ": Note has been removed");
    }
}
