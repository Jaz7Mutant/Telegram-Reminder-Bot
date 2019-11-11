package reminder;

import inputOutput.UserIO;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static reminder.AddingState.*;

public class NoteKeeper {
    public UserState currentState = UserState.IDLE;
    public AddingState addingState;
    private UserIO userIO;
    private String chatId;
    private Reminder reminder;

    private Calendar newRawDate = new GregorianCalendar();
    private Calendar newRawRemindDate = new GregorianCalendar();
    private String newNoteText = null;
    private LocalDateTime newNoteDate = null;
    private LocalDateTime newNoteRemindDate = null;
    private NoteSerializer noteSerializer;

    public NoteKeeper(String chatId, UserIO userIO, Reminder reminder, NoteSerializer noteSerializer) {
        this.reminder = reminder;
        this.chatId = chatId;
        this.userIO = userIO;
        this.noteSerializer = noteSerializer;
    }

    public void doNextStep(String userMessage) {
        switch (currentState) {
            case IDLE:
                return;
            case ADDING:
                doNextAddingStep(userMessage);
                return;
            case SHOWING:
                currentState = NotePrinter.showUsersNotes(userMessage, chatId, reminder, currentState);
                return;
            case REMOVING:
                removeNote(userMessage);
        }
    }

    private void doNextAddingStep(String userMessage) {
        switch (addingState) {
            case SET_TEXT:
                if (userMessage == null) {
                    userIO.showMessage("Wrong format", chatId);
                    userIO.showMessage("Write your note", chatId);
                    return;
                } else {
                    newNoteText = userMessage;
                    userIO.showMessage("When will it happen?", chatId);
                    userIO.showOnClickButton("Choose year", DateTimeParser.years, chatId);
                    addingState = SET_YEAR;
                    return;
                }
            case SET_YEAR:
                addingState = DateTimeParser.setYear(newRawDate, userMessage, chatId, addingState);
                return;
            case SET_REMIND_YEAR:
                addingState = DateTimeParser.setYear(newRawRemindDate, userMessage, chatId, addingState);
                return;
            case SET_MONTH:
                addingState = DateTimeParser.setMonth(newRawDate, userMessage, chatId, addingState);
                return;
            case SET_REMIND_MONTH:
                addingState = DateTimeParser.setMonth(newRawRemindDate, userMessage, chatId, addingState);
                return;
            case SET_DAY:
                addingState = DateTimeParser.setDay(newRawDate,userMessage, chatId, addingState);
                return;
            case SET_REMIND_DAY:
                addingState = DateTimeParser.setDay(newRawRemindDate,userMessage, chatId, addingState);
                return;
            case SET_TIME:
                addingState = DateTimeParser.setTime(newRawDate,userMessage,chatId,addingState);
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
                addingState = DateTimeParser.setTime(newRawRemindDate,userMessage,chatId,addingState);
                if (addingState == IDLE) {
                    finishAddNote();
                }
                return;
            case SET_REMIND:
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
        synchronized (reminder.notes) {
            reminder.notes.add(new Note(
                    chatId,
                    newNoteText,
                    newNoteDate,
                    newNoteRemindDate));
            reminder.notePrinter.run();
            addingState = AddingState.IDLE;
            currentState = UserState.IDLE;
            int stringLimit = 20;
            if (newNoteText.length() < 20) {
                stringLimit = newNoteText.length();
            }
            userIO.showMessage("You have a new note \""
                    + newNoteText.substring(0, stringLimit) + "...\" with remind on " + newNoteRemindDate.format(NotePrinter.dateTimeFormatter), chatId);
            noteSerializer.serializeNotes(reminder.notes);
        }
    }

    //todo lock
    private synchronized void removeNote(String userMessage){
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
    }
}
