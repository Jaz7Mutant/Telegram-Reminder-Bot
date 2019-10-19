//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.YearMonth;
//import java.util.Calendar;
//
//public class DateTimeParser {
//    private static LocalDateTime currentDate;
//    public static String[] years;
//    private static String[] months;
//    private static String[] days;
//    private static UserIO userIO;
//
//    public DateTimeParser(UserIO userIO) {
//        this.userIO = userIO;
//    }
//
//    public static void updateCurrentDate() {
//        currentDate = LocalDateTime.now();
//        years = new String[]{
//                Integer.toString(currentDate.getYear()),
//                Integer.toString(currentDate.plusYears(1).getYear()),
//                "Other"};
//        months = new String[12];
//        for (int i = 0; i < 12; i++) {
//            months[i] = currentDate.plusMonths(i).getMonth().toString();
//        }
//    }
//
//    public static void setYear(Calendar rawDate, String userMessage, String chatId, AddingStates addingState) {
//        int respond;
//        try {
//            respond = Integer.parseInt(userMessage);
//        } catch (NumberFormatException e) {
//            userIO.showMessage("Wrong format", chatId);
//            return;
//        }
//        if (respond == years.length - 1) {
//            userIO.showMessage("Set year (yyyy)", chatId);
//            return;
//        }
//        if (respond < years.length) {
//            respond = Integer.parseInt(years[respond]);
//        } else if (respond < currentDate.getYear() || respond > 2035) {
//            userIO.showMessage("Illegal year", chatId);
//            return;
//        }
//        rawDate.set(Calendar.YEAR, respond);
//        userIO.showOnClickButton("Choose month", months, chatId);
//        if (addingState == AddingStates.SET_YEAR) {
//            addingState = AddingStates.SET_MONTH;
//        } else {
//            addingState = AddingStates.SET_REMIND_MONTH;
//        }
//    }
//
//    public static void setMonth(Calendar rawDate, String userMessage, String chatId, AddingStates addingState) {
//        int respond;
//        try {
//            respond = Integer.parseInt(userMessage);
//            if (respond > 11) {
//                throw new NumberFormatException();
//            }
//        } catch (NumberFormatException e) {
//            userIO.showMessage("Wrong format", chatId);
//            return;
//        }
//        rawDate.set(Calendar.MONTH, currentDate.plusMonths(respond - 1).getMonthValue());
//
//        int daysInMonth = getDaysInMonth(rawDate);
//        days = new String[daysInMonth];
//        for (int i = 1; i <= daysInMonth; i++) {
//            days[i - 1] = Integer.toString(i);
//        }
//        userIO.showOnClickButton("Choose day", days, chatId);
//        if (addingState == AddingStates.SET_MONTH) {
//            addingState = AddingStates.SET_DAY;
//        } else {
//            addingState = AddingStates.SET_REMIND_DAY;
//        }
//    }
//
//    public static void setDay(Calendar rawDate, String userMessage, String chatId, AddingStates addingState) {
//        int respond;
//        try {
//            respond = Integer.parseInt(userMessage);
//            if (respond > days.length) {
//                throw new NumberFormatException();
//            }
//        } catch (NumberFormatException e) {
//            userIO.showMessage("Wrong format", chatId);
//            return;
//        }
//        rawDate.set(Calendar.DAY_OF_MONTH, respond + 1);
//
//        userIO.showMessage("Set time (hh:mm)", chatId);
//        if (addingState == AddingStates.SET_DAY) {
//            addingState = AddingStates.SET_TIME;
//        } else {
//            addingState = AddingStates.SET_REMIND_TIME;
//        }
//    }
//
//    public static void setTime(
//            Calendar rawDate,
//            String userMessage,
//            String chatId,
//            AddingStates addingState,
//            LocalDateTime newNoteDate,
//            Calendar newRawDate,
//            LocalDateTime newNoteRemindDate,
//            Calendar newRawRemindDate) {
//        LocalTime time;
//        try {
//            time = LocalTime.parse(userMessage);
//        } catch (Exception e) {
//            userIO.showMessage("Wrong format", chatId);
//            return;
//        }
//        rawDate.set(Calendar.HOUR_OF_DAY, time.getHour());
//        rawDate.set(Calendar.MINUTE, time.getMinute());
//        if (addingState == AddingStates.SET_TIME) {
//            addingState = AddingStates.SET_REMIND;
//            newNoteDate = LocalDateTime.ofInstant(newRawDate.toInstant(), newRawDate.getTimeZone().toZoneId());
//            userIO.showOnClickButton("Set the date of remind", new String[]{
//                    "No remind",
//                    "A hour before",
//                    "A day before",
//                    "A week before",
//                    "Set date..."}, chatId);
//        } else {
//            newNoteRemindDate = LocalDateTime.ofInstant(newRawRemindDate.toInstant(), newRawRemindDate.getTimeZone().toZoneId());
//        }
//    }
//
//    private static int getDaysInMonth(Calendar calendar) {
//        return YearMonth.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)).lengthOfMonth();
//    }
//}