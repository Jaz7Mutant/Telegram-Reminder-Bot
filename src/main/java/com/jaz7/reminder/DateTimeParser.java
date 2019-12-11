package com.jaz7.reminder;

import com.jaz7.bot.BotOptions;
import com.jaz7.inputOutput.UserIO;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class DateTimeParser {
    public static String[] years;
    public static LocalDateTime currentDate;
    private static String[] currentYearMonths;
    private static String[] days;
    private static UserIO userIO;
    private static final Logger LOGGER = Logger.getLogger(DateTimeParser.class.getSimpleName());

    public DateTimeParser(UserIO userIO) {
        DateTimeParser.userIO = userIO;
        LOGGER.info("DateTimeParser has been created");
    }

    public static void updateCurrentDate() {
        currentDate = LocalDateTime.now();
        years = new String[]{
                Integer.toString(currentDate.getYear()),
                Integer.toString(currentDate.plusYears(1).getYear()),
                "Other"};
        if (currentDate.getMonth().getValue() == 1)
            currentYearMonths = Arrays.stream(Month.values()).map(Enum::toString).toArray(String[]::new);
        else {
            List<String> yearMonths = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Month month = currentDate.plusMonths(i).getMonth();
                if (month.getValue() != 1)
                    yearMonths.add(month.toString());
                else break;
            }
            currentYearMonths = yearMonths.toArray(new String[0]);
        }
        LOGGER.info("Current date has been updated");
    }

    public static AddingState setYear(Calendar rawDate, int year, String chatId, AddingState addingState) {
        rawDate.set(Calendar.YEAR, year);
        LOGGER.info(String.format("%s: Year has been set", chatId));
        if (year == currentDate.getYear())
            userIO.showOnClickButton(BotOptions.botAnswers.get("ChooseMonth"), currentYearMonths, chatId);
        else {
            String[] yearMonths = Arrays.stream(Month.values()).map(Enum::toString).toArray(String[]::new);
            userIO.showOnClickButton(BotOptions.botAnswers.get("ChooseMonth"), yearMonths, chatId);
        }
        if (addingState == AddingState.SET_YEAR) {
            return AddingState.SET_MONTH;
        } else {
            return AddingState.SET_REMIND_MONTH;
        }
    }

    public static AddingState setMonth(Calendar rawDate, int month, String chatId, AddingState addingState) {
        rawDate.set(Calendar.MONTH, month - 1);
        LOGGER.info(String.format("%s: Month has been set", chatId));
        int daysInMonth = getDaysInMonth(rawDate);
        Reminder.users.get(chatId).noteKeeper.noteAdder.daysInCurrentMonth = daysInMonth;
        //todo разобраться зачем здесь это присовение, какое дальнейший смысл оно имеет
        String[] daysForMonth = getDaysForMonth(rawDate, daysInMonth);
        userIO.showOnClickButton(BotOptions.botAnswers.get("ChooseDay"), daysForMonth, chatId);
        if (addingState == AddingState.SET_MONTH) {
            return AddingState.SET_DAY;
        } else {
            return AddingState.SET_REMIND_DAY;
        }
    }

    private static String[] getDaysForMonth(Calendar rawDate, int daysInMonth) {
        List<String> days = new ArrayList<>();
        if (currentDate.getMonthValue() - 1 != rawDate.get(Calendar.MONTH) ||
                currentDate.getYear() != rawDate.get(Calendar.YEAR))
            rawDate.set(Calendar.DAY_OF_MONTH, 1);
        int startDayOfWeek = rawDate.get(Calendar.DAY_OF_WEEK) == 1 ? 7 : rawDate.get(Calendar.DAY_OF_WEEK) - 1;
        for (int i = 1; i < startDayOfWeek; i++)
            days.add(" ");
        for (int i = rawDate.get(Calendar.DAY_OF_MONTH); i <= daysInMonth; i++)
            days.add(Integer.toString(i));
        int daysInLastWeek = days.size() / 7 * 7 == days.size() ? 7 : days.size() - days.size() / 7 * 7;
        for (int i = daysInLastWeek; i < 7; i++)
            days.add(" ");
        return days.toArray(String[]::new);
    }

    public static AddingState setDay(Calendar rawDate, int day, String chatId, AddingState addingState) {
        rawDate.set(Calendar.DAY_OF_MONTH, day);
        LOGGER.info(String.format("%s: Day has been set", chatId));
        userIO.showMessage(BotOptions.botAnswers.get("SetTime"), chatId);
        if (addingState == AddingState.SET_DAY) {
            return AddingState.SET_TIME;
        } else {
            return AddingState.SET_REMIND_TIME;
        }
    }

    public static AddingState setTime(Calendar rawDate, LocalTime time, String chatId, AddingState addingState) {
        rawDate.set(Calendar.HOUR_OF_DAY, time.getHour());
        rawDate.set(Calendar.MINUTE, time.getMinute());
        // Если время в прошлом, то заново задаем всю дату, начиная с месяца
        if (LocalDateTime.ofInstant( // TODO Исправить - Сделать так, чтобы новая дата задавалась с шага, на котором была сделана ошибка. Т.е. Добавить на каждом шаге проверку
                rawDate.toInstant(),
                rawDate.getTimeZone().toZoneId()).isBefore(LocalDateTime.now())) {
            userIO.showMessage(BotOptions.botAnswers.get("WrongDate"), chatId);
            userIO.showOnClickButton(BotOptions.botAnswers.get("ChooseMonth"), currentYearMonths, chatId);
            // todo Разобраться с currentYearMonths, какое оно тут должно быть
            LOGGER.info(String.format("%s: Wrong time (in the past)", chatId));
            if (addingState == AddingState.SET_TIME) {
                return AddingState.SET_MONTH;
            } else {
                return AddingState.SET_REMIND_MONTH;
            }
        }
        LOGGER.info(String.format("%s: Time has been set", chatId));

        if (addingState == AddingState.SET_TIME) {
            return AddingState.SET_REMIND;
        } else {
            return AddingState.SET_REPEATING_PERIOD;
        }
    }

    private static int getDaysInMonth(Calendar calendar) {
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
}