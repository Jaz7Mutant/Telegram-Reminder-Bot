import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class StateHolder {
    public UserStates currentState = UserStates.IDLE;
    private UserIO userIO;
    private String chatId;
    private NoteMaker noteMaker;

    private static LocalDateTime currentDate;
    public static String[] years;
    private static String[] months;
    private static String[] days;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private Calendar newRawDate = new GregorianCalendar();
    private Calendar newRawRemindDate = new GregorianCalendar();
    private String newNoteText = null;
    private LocalDateTime newNoteDate = null;
    private LocalDateTime newNoteRemindDate = null;
    public AddingStates addingState;

    public StateHolder(String chatId, UserIO userIO, NoteMaker noteMaker) {
        this.noteMaker = noteMaker;
        this.chatId = chatId;
        this.userIO = userIO;
    }

    public void updateCurrentDate() {
        currentDate = LocalDateTime.now();
        years = new String[]{
                Integer.toString(currentDate.getYear()),
                Integer.toString(currentDate.plusYears(1).getYear()),
                "Other"};
        months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = currentDate.plusMonths(i).getMonth().toString();
        }
    }

    public void doNextStep(String s) {
        switch (currentState) {
            case IDLE:
                return;

            case ADDING:
                doNextAddingStep(s);
                return;

            case SHOWING:
                int respond;
                List<Note> userNotes = new ArrayList<>();
                for (Note note : noteMaker.notes) {
                    if (note.getChatId().equals(chatId)) {
                        userNotes.add(note);
                    }
                }
                if (userNotes.size() == 0){
                    userIO.showMessage("You have no notes", chatId);
                    currentState = UserStates.IDLE;
                    return;
                }
                try {
                    respond = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    userIO.showMessage("Wrong format", chatId);
                    return;
                }

                switch (respond) {
                    case 0:
                        NotePrinter.printTodayNotes(userNotes, chatId);
                        break;
                    case 1:
                        NotePrinter.printTenUpcomingNotes(userNotes, chatId);
                        break;
                    case 2:
                        NotePrinter.printAllNotes(userNotes, chatId);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + respond);
                }
                currentState = UserStates.IDLE;
            case REMOVING:

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
                    userIO.showOnClickButton("Choose year", years, chatId);
                    addingState = AddingStates.SET_YEAR;
                    return;
                }
            case SET_YEAR:
                setYear(newRawDate, userMessage);
                return;
            case SET_REMIND_YEAR:
                setYear(newRawRemindDate, userMessage);
                return;
            case SET_MONTH:
                setMonth(newRawDate, userMessage);
                return;
            case SET_REMIND_MONTH:
                setMonth(newRawRemindDate, userMessage);
                return;
            case SET_DAY:
                setDay(newRawDate, userMessage);
                return;
            case SET_REMIND_DAY:
                setDay(newRawRemindDate, userMessage);
                return;
            case SET_TIME:
                setTime(newRawDate, userMessage);
                return;
            case SET_REMIND_TIME:
                setTime(newRawRemindDate, userMessage);
                return;
            case SET_REMIND:
                setRemind(userMessage);
        }
    }

    private void setYear(Calendar rawDate, String userMessage){
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
        } catch (NumberFormatException e) {
            userIO.showMessage("Wrong format", chatId);
            return;
        }
        if (respond == years.length - 1) {
            userIO.showMessage("Set year (yyyy)", chatId);
            return;
        }
        if (respond < years.length){
            respond = Integer.parseInt(years[respond]);
        }
        else if (respond < currentDate.getYear() || respond > 2035) {
            userIO.showMessage("Illegal year", chatId);
            return;
        }
        rawDate.set(Calendar.YEAR, respond);
        userIO.showOnClickButton("Choose month", months, chatId);
        if (addingState == AddingStates.SET_YEAR) {
            addingState = AddingStates.SET_MONTH;
        }
        else {
            addingState = AddingStates.SET_REMIND_MONTH;
        }
    }

    private void setMonth(Calendar rawDate, String userMessage){
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
            if (respond > 11) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            userIO.showMessage("Wrong format", chatId);
            return;
        }
        rawDate.set(Calendar.MONTH, currentDate.plusMonths(respond - 1).getMonthValue());

        int daysInMonth = getDaysInMonth(rawDate);
        days = new String[daysInMonth];
        for (int i = 1; i <= daysInMonth; i++) {
            days[i - 1] = Integer.toString(i);
        }
        userIO.showOnClickButton("Choose day", days, chatId);
        if (addingState == AddingStates.SET_MONTH) {
            addingState = AddingStates.SET_DAY;
        }
        else{
            addingState = AddingStates.SET_REMIND_DAY;
        }
    }

    private void setDay(Calendar rawDate,String userMessage){
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
            if (respond > days.length) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            userIO.showMessage("Wrong format", chatId);
            return;
        }
        rawDate.set(Calendar.DAY_OF_MONTH, respond + 1);

        userIO.showMessage("Set time (hh:mm)", chatId);
        if (addingState == AddingStates.SET_DAY) {
            addingState = AddingStates.SET_TIME;
        }
        else {
            addingState = AddingStates.SET_REMIND_TIME;
        }
    }

    private  void setTime(Calendar rawDate, String userMessage){
        LocalTime time;
        try {
            time = LocalTime.parse(userMessage);
        } catch (Exception e) {
            userIO.showMessage("Wrong format", chatId);
            return;
        }
        rawDate.set(Calendar.HOUR_OF_DAY, time.getHour());
        rawDate.set(Calendar.MINUTE, time.getMinute());
        newNoteDate = LocalDateTime.ofInstant(newRawDate.toInstant(), newRawDate.getTimeZone().toZoneId());
        if (addingState == AddingStates.SET_TIME){
            addingState = AddingStates.SET_REMIND;

            userIO.showOnClickButton("Set the date of remind", new String[]{
                    "No remind",
                    "A hour before",
                    "A day before",
                    "A week before",
                    "Set date..."}, chatId);
        }
        else{
            newNoteRemindDate = LocalDateTime.ofInstant(newRawRemindDate.toInstant(), newRawRemindDate.getTimeZone().toZoneId());
            finishAddNote();
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
                userIO.showOnClickButton("Choose year", years, chatId);
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
        NoteMaker.userStates.get(chatId).currentState = UserStates.IDLE;
        int stringLimit = 20;
        if (newNoteText.length() < 20) {
            stringLimit = newNoteText.length();
        }
        userIO.showMessage("You have a new note \""
                + newNoteText.substring(0, stringLimit) + "...\" with remind on " + newNoteRemindDate.format(dateTimeFormatter), chatId);
    }

    private int getDaysInMonth(Calendar calendar) {
        return YearMonth.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)).lengthOfMonth();
    }
}
