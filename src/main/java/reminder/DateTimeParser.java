package reminder;

import bot.BotOptions;
import inputOutput.UserIO;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;

public class DateTimeParser {
    public static String[] years;
    private static LocalDateTime currentDate;
    private static String[] months;
    private static String[] days;
    private static UserIO userIO;

    public DateTimeParser(UserIO userIO) {
        DateTimeParser.userIO = userIO;
    }

    public static void updateCurrentDate() {
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

    public static AddingState setYear(Calendar rawDate, String userMessage, String chatId, AddingState addingState) {
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
        } catch (NumberFormatException e) {
            userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), chatId);
            return addingState;
        }
        if (respond == years.length - 1) {
            userIO.showMessage(BotOptions.botAnswers.get("SetYear"), chatId);
            return addingState;
        }
        if (respond < years.length) {
            respond = Integer.parseInt(years[respond]);
        } else if (respond < currentDate.getYear() || respond > 2035) {
            userIO.showMessage(BotOptions.botAnswers.get("IllegalYear"), chatId);
            return addingState;
        }
        rawDate.set(Calendar.YEAR, respond);
        userIO.showOnClickButton(BotOptions.botAnswers.get("ChooseMonth"), months, chatId);
        if (addingState == AddingState.SET_YEAR) {
            return AddingState.SET_MONTH;
        } else {
            return AddingState.SET_REMIND_MONTH;
        }
    }

    public static AddingState setMonth(Calendar rawDate, String userMessage, String chatId, AddingState addingState) {
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
            if (respond > 11 || respond < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), chatId);
            return addingState;
        }
        rawDate.set(Calendar.MONTH, currentDate.plusMonths(respond - 1).getMonthValue());

        int daysInMonth = getDaysInMonth(rawDate);
        days = new String[daysInMonth];
        for (int i = 1; i <= daysInMonth; i++) {
            days[i - 1] = Integer.toString(i);
        }
        userIO.showOnClickButton(BotOptions.botAnswers.get("ChooseDay"), days, chatId);
        if (addingState == AddingState.SET_MONTH) {
            return AddingState.SET_DAY;
        } else {
            return AddingState.SET_REMIND_DAY;
        }
    }

    public static AddingState setDay(Calendar rawDate, String userMessage, String chatId, AddingState addingState) {
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
            if (respond > days.length || respond < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), chatId);
            return addingState;
        }
        rawDate.set(Calendar.DAY_OF_MONTH, respond + 1);

        userIO.showMessage(BotOptions.botAnswers.get("SetTime"), chatId);
        if (addingState == AddingState.SET_DAY) {
            return AddingState.SET_TIME;
        } else {
            return AddingState.SET_REMIND_TIME;
        }
    }

    public static AddingState setTime(Calendar rawDate, String userMessage, String chatId, AddingState addingState) {
        LocalTime time;
        try {
            time = LocalTime.parse(userMessage);
        } catch (Exception e) {
            userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), chatId);
            return addingState;
        }
        rawDate.set(Calendar.HOUR_OF_DAY, time.getHour());
        rawDate.set(Calendar.MINUTE, time.getMinute());
        if (LocalDateTime.ofInstant(
                rawDate.toInstant(),
                rawDate.getTimeZone().toZoneId()).isBefore(LocalDateTime.now())) {
            userIO.showMessage(BotOptions.botAnswers.get("WrongDate"), chatId);
            userIO.showOnClickButton(BotOptions.botAnswers.get("ChooseMonth"), months, chatId);
            if (addingState == AddingState.SET_TIME){
                return AddingState.SET_MONTH;
            }
            else {
                return AddingState.SET_REMIND_MONTH;
            }
        }
        if (addingState == AddingState.SET_TIME) {
            return AddingState.SET_REMIND;
        } else {
            return AddingState.IDLE;
        }
    }

    private static int getDaysInMonth(Calendar calendar) {
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
}