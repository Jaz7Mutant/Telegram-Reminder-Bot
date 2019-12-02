package com.jaz7.reminder;
import com.jaz7.bot.BotOptions;
import com.jaz7.inputOutput.UserIO;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.logging.Logger;

public class DateTimeParser {
    public static String[] years;
    public static LocalDateTime currentDate;
    private static String[] months;
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
        months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = currentDate.plusMonths(i).getMonth().toString();
        }
        LOGGER.info("Current date has been updated");
    }

    public static AddingState setYear(Calendar rawDate, int year, String chatId, AddingState addingState) {
        rawDate.set(Calendar.YEAR, year);
        LOGGER.info(chatId + ": Year has been set");
        
        userIO.showOnClickButton(BotOptions.botAnswers.get("ChooseMonth"), months, chatId);
        if (addingState == AddingState.SET_YEAR) {
            return AddingState.SET_MONTH;
        } else {
            return AddingState.SET_REMIND_MONTH;
        }
    }

    public static AddingState setMonth(Calendar rawDate, int month, String chatId, AddingState addingState) {
        rawDate.set(Calendar.MONTH, currentDate.plusMonths(month).getMonthValue());
        LOGGER.info(chatId + ": Month has been set");

        int daysInMonth = getDaysInMonth(rawDate);
        days = new String[daysInMonth];
        for (int i = 1; i <= daysInMonth; i++) {
            days[i - 1] = Integer.toString(i);
        }
        Reminder.users.get(chatId).noteKeeper.noteAdder.daysInCurrentMonth = days;
        userIO.showOnClickButton(BotOptions.botAnswers.get("ChooseDay"), days, chatId);
        if (addingState == AddingState.SET_MONTH) {
            return AddingState.SET_DAY;
        } else {
            return AddingState.SET_REMIND_DAY;
        }
    }

    public static AddingState setDay(Calendar rawDate, int day, String chatId, AddingState addingState) {
        rawDate.set(Calendar.DAY_OF_MONTH, day);
        LOGGER.info(chatId + ": Day has been set");
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
            userIO.showOnClickButton(BotOptions.botAnswers.get("ChooseMonth"), months, chatId);
            LOGGER.info(chatId + ": Wrong time (in the past)");
            if (addingState == AddingState.SET_TIME){
                return AddingState.SET_MONTH;
            }
            else {
                return AddingState.SET_REMIND_MONTH;
            }
        }
        LOGGER.info(chatId + ": Time has been set");

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