import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class StateHolder {
    public UserStates currentState = UserStates.IDLE;
    private UserIO userIO;
    private long chatId;
    private NoteMaker noteMaker;

    private LocalDateTime currentDate;
    private String[] years;
    private String[] months;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private String newNoteText = null;
    private int newRawYear = -1;
    private int newRawMonth = -1;
    private int newRawDay = -1;
    private LocalTime newRawTime = null;
    private boolean settingTimeInProcess = false;

    private LocalDateTime newNoteDate = null;
    private LocalDateTime newNoteRemindDate = null;

    public StateHolder(long chatId, UserIO userIO, NoteMaker noteMaker) {
        this.noteMaker = noteMaker;
        this.chatId = chatId;
        this.userIO = userIO;
    }

    public void doNextStep(String s) {
        switch (currentState) {
            case IDLE:
                return;

            case ADDING:
                currentDate = LocalDateTime.now();
                years = new String[]{
                        Integer.toString(currentDate.getYear()),
                        Integer.toString(currentDate.plusYears(1).getYear()),
                        "Other"};
                months = new String[12];
                for (int i = 0; i < 12; i++) {
                    months[i] = currentDate.plusMonths(i).getMonth().toString();
                }
                doNextAddingStep(s);

            case SHOWING:
        }
    }

    private void doNextAddingStep(String userMessage) {
        if (newNoteText == null) {
            newNoteText = userMessage;
            userIO.showMessage("When will it happen?", chatId);
            userIO.showMessage("Choose year", chatId);
            userIO.getOnClickButton(years, chatId);
            return;
        }

        if (newNoteDate == null) {
            getDateTime(userMessage);
            return;
        }

        if (newNoteRemindDate == null) {
            int respond = Integer.parseInt(userMessage);
            if (settingTimeInProcess) {
                getDateTime(userMessage);
            }
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
                    userIO.showMessage("Choose year", chatId);
                    userIO.getOnClickButton(years, chatId);
                    settingTimeInProcess = true;
                    getDateTime(userMessage);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            return;
        }

        noteMaker.notes.add(new Note(
                chatId,
                newNoteText,
                newNoteDate,
                newNoteRemindDate));
        noteMaker.notePrinter.run();
        int stringLimit = 20;
        if (newNoteText.length() < 20) {
            stringLimit = newNoteText.length();
        }
        userIO.showMessage("You have a new note \""
                + newNoteText.substring(0, stringLimit) + "...\" with remind on " + newNoteRemindDate.format(dateTimeFormatter), chatId);

        newNoteText = null;
        newRawYear = -1;
        newRawMonth = -1;
        newRawDay = -1;
        newRawTime = null;
        settingTimeInProcess = false;
        newNoteDate = null;
        newNoteRemindDate = null;
        currentState = UserStates.IDLE;
    }

    private void getDateTime(String userMessage) {

        if (newRawYear == -1) {
            int respond = Integer.parseInt(userMessage);
            if (respond >= years.length - 1) {
                try {
                    userIO.showMessage("Set year (yyyy)", chatId);
                    if (respond == years.length - 1)
                        return;
                    if (respond < currentDate.getYear() || respond > 2035) {
                        throw new ParseException("Illegal year", 1);
                    }
                    newRawYear = respond;
                } catch (ParseException e) {
                    userIO.showMessage("Wrong format", chatId);
                    return;
                }
            } else {
                newRawYear = Integer.parseInt(years[respond]);
            }
            userIO.showMessage("Choose Month", chatId);
            userIO.getOnClickButton(months, chatId);
            return;
        }

        if (newRawMonth == -1) {
            int respond = Integer.parseInt(userMessage);
            newRawMonth = currentDate.plusMonths(respond - 1).getMonthValue();

            userIO.showMessage("choose day", chatId);
            Calendar _cal = new GregorianCalendar();
            _cal.set(Calendar.YEAR, newRawYear);
            _cal.set(Calendar.MONTH, newRawMonth);
            int daysInMonth = YearMonth.of(_cal.get(Calendar.YEAR), _cal.get(Calendar.MONTH)).lengthOfMonth();
            String[] days = new String[daysInMonth];
            for (int i = 1; i <= daysInMonth; i++) {
                days[i - 1] = Integer.toString(i);
            }
            userIO.getOnClickButton(days, chatId);
            return;
        }

        if (newRawDay == -1) {
            int respond = Integer.parseInt(userMessage);
            newRawDay = respond + 1;
            userIO.showMessage("Set time (hh:mm)", chatId);
            return;
        }

        if (newRawTime == null) {
            LocalTime time;
            try {
                time = LocalTime.parse(userMessage);
            } catch (Exception e) {
                userIO.showMessage("Wrong format", chatId);
                return;
            }
            newRawTime = time;
            return;
        }

        Calendar userDate = new GregorianCalendar();
        userDate.set(Calendar.YEAR, newRawYear);
        userDate.set(Calendar.MONTH, newRawMonth);
        userDate.set(Calendar.DAY_OF_MONTH, newRawDay);
        userDate.set(Calendar.HOUR_OF_DAY, newRawTime.getHour());
        userDate.set(Calendar.MINUTE, newRawTime.getMinute());
        if (newNoteDate == null) {
            newNoteDate = LocalDateTime.ofInstant(userDate.toInstant(), userDate.getTimeZone().toZoneId());
            userIO.showMessage("Set the date of remind", chatId);
            userIO.getOnClickButton(new String[]{
                    "No remind",
                    "A hour before",
                    "A day before",
                    "A week before",
                    "Set date..."}, chatId);
            newRawTime = null;
            newRawDay = -1;
            newRawMonth = -1;
            newRawYear = -1;
            return;
        }

        if (newNoteRemindDate == null) {
            newNoteRemindDate = newNoteDate = LocalDateTime.ofInstant(userDate.toInstant(), userDate.getTimeZone().toZoneId());
            newRawTime = null;
            newRawDay = -1;
            newRawMonth = -1;
            newRawYear = -1;
        }
    }
}
