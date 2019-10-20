import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class StateHolder {
    public UserStates currentState = UserStates.IDLE;
    public AddingStates addingState;
    private UserIO userIO;
    private String chatId;
    private NoteMaker noteMaker;

    private Calendar newRawDate = new GregorianCalendar();
    private Calendar newRawRemindDate = new GregorianCalendar();
    private String newNoteText = null;
    private LocalDateTime newNoteDate = null;
    private LocalDateTime newNoteRemindDate = null;

    public StateHolder(String chatId, UserIO userIO, NoteMaker noteMaker) {
        this.noteMaker = noteMaker;
        this.chatId = chatId;
        this.userIO = userIO;
    }

    public void doNextStep(String userMessage) {
        switch (currentState) {
            case IDLE:
                return;
            case ADDING:
                doNextAddingStep(userMessage);
                return;
            case SHOWING:
                currentState = NotePrinter.showUsersNotes(userMessage, chatId, noteMaker, currentState);
                return;
            case REMOVING:
                removeNote(userMessage);
                return;
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
                    addingState = AddingStates.SET_YEAR;
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
                if (addingState == AddingStates.SET_REMIND){
                    newNoteDate = LocalDateTime.ofInstant(newRawDate.toInstant(), newRawDate.getTimeZone().toZoneId());
                    userIO.showOnClickButton("Set the date of remind", new String[]{
                            "No remind",
                            "A hour before",
                            "A day before",
                            "A week before",
                            "Set date..."}, chatId);
                }else if (addingState == AddingStates.IDLE){
                    finishAddNote();
                }
                return;
            case SET_REMIND_TIME:
                addingState = DateTimeParser.setTime(newRawRemindDate,userMessage,chatId,addingState);
                finishAddNote();
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
                addingState = AddingStates.SET_REMIND_YEAR;
                return;
            default:
                throw new IllegalArgumentException();
        }
        finishAddNote();
    }

    private void finishAddNote(){
        noteMaker.notes.add(new Note(
                chatId,
                newNoteText,
                newNoteDate,
                newNoteRemindDate));
        noteMaker.notePrinter.run();
        addingState = AddingStates.IDLE;
        currentState = UserStates.IDLE;
        int stringLimit = 20;
        if (newNoteText.length() < 20) {
            stringLimit = newNoteText.length();
        }
        userIO.showMessage("You have a new note \""
                + newNoteText.substring(0, stringLimit) + "...\" with remind on " + newNoteRemindDate.format(NotePrinter.dateTimeFormatter), chatId);
    }

    private void removeNote(String userMessage){
        int respond;
        List<Note> userNotes = NotePrinter.getUserNotes(noteMaker, chatId);
        try {
            respond = Integer.parseInt(userMessage);
            if (respond > userNotes.size() || respond <= 0){
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            userIO.showMessage("Wrong format", chatId);
            return;
        }
        noteMaker.notes.remove(userNotes.get(respond - 1));
        currentState = UserStates.IDLE;
        userIO.showMessage("Note has been removed", chatId);
    }
}
