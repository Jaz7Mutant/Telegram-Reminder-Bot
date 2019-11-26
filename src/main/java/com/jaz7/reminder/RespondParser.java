package com.jaz7.reminder;

import com.jaz7.inputOutput.UserIO;

import java.time.LocalTime;
import java.util.List;
import java.util.logging.Logger;

import static com.jaz7.reminder.DateTimeParser.currentDate;
import static com.jaz7.reminder.DateTimeParser.years;

public class RespondParser {
    public static UserIO userIO;
    private static final Logger LOGGER = Logger.getLogger(RespondParser.class.getSimpleName());

    public RespondParser(UserIO userIO){
        this.userIO = userIO;
    }

    public static int parseSetYearRespond(String userMessage, String chatId){
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
        } catch (NumberFormatException e) {
            LOGGER.info(chatId + ": Wrong year format");
            throw new IllegalArgumentException();
        }
        if (respond == years.length - 1) {
            LOGGER.info(chatId + ": Setting year manually");
            return 0;
        }
        if (respond < years.length) {
            respond = Integer.parseInt(years[respond]);
        } else if (respond < currentDate.getYear() || respond > 2035) {

            LOGGER.info(chatId + ": Illegal year");
            throw new IllegalArgumentException();
        }
        return respond;
    }

    public static int parseSetMonthRespond(String userMessage, String chatId){
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
            if (respond > 11 || respond < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            LOGGER.info(chatId + ": Wrong month format");
            throw new IllegalArgumentException();
        }
        return respond;
    }

    public static int parseSetDayRespond(String userMessage, String chatId, String[] days){
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
            if (respond > days.length || respond < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            LOGGER.info(chatId + ": Wrong day format");
            throw new IllegalArgumentException();
        }
        return respond;
    }

    public static LocalTime parseSerTimeRespond(String userMessage, String chatId){
        LocalTime time;
        try {
            time = LocalTime.parse(userMessage);
        } catch (Exception e) {
            LOGGER.info(chatId + ": Wrong time format");
            throw new IllegalArgumentException();
        }
        return time;
    }

    public static int parseSetRemindRespond(String userMessage, String chatId){
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
            if (respond > 7){
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e){
            LOGGER.info(chatId + ": Wrong format in setting remind");
            throw new IllegalArgumentException();
        }
        return respond;
    }

    public static int parseRemoveNoteRespond(String userMessage, List<Note> userNotes, String chatId){
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
            if (respond > userNotes.size() || respond <= 0){
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            LOGGER.info(chatId + ": Wrong format in removing notes");
            throw new IllegalArgumentException();
        }
        return respond;
    }
}
