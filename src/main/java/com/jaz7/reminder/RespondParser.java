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
        LOGGER.info("Initializing RespondParser");
        this.userIO = userIO;
    }

    public static int parseSetYearRespond(String userMessage, String chatId){
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
        } catch (NumberFormatException e) {
            LOGGER.info(chatId + ": Wrong year format");
            return -1;
        }
        if (respond == years.length - 1) {
            LOGGER.info(chatId + ": Setting year manually");
            return 0;
        }
        if (respond < years.length) {
            respond = Integer.parseInt(years[respond]);
        } else if (respond < currentDate.getYear() || respond > 2035) {
            LOGGER.info(chatId + ": Illegal year");
            return -1;
        }
        return respond;
    }

    public static int parseSetMonthRespond(String userMessage, String chatId){
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
            if (respond > 11 || respond < 0) {
                return -1;
            }
        } catch (NumberFormatException e) {
            LOGGER.info(chatId + ": Wrong month format");
            return -1;
        }
        return respond;
    }

    public static int parseSetDayRespond(String userMessage, String chatId, String[] days){
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
            if (respond > days.length || respond < 0) {
                return -1;
            }
        } catch (NumberFormatException e) {
            LOGGER.info(chatId + ": Wrong day format");
            return -1;
        }
        return respond;
    }

    public static LocalTime parseSetTimeRespond(String userMessage, String chatId){
        LocalTime time;
        try {
            time = LocalTime.parse(userMessage);
        } catch (Exception e) {
            LOGGER.info(chatId + ": Wrong time format");
            return null;
        }
        return time;
    }

    public static int parseSetRemindRespond(String userMessage, String chatId){
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
            if (respond > NoteAdder.remindTypes.length - 1){
                return -1;
            }
        }
        catch (NumberFormatException e){
            LOGGER.info(chatId + ": Wrong format in setting remind");
            return -1;
        }
        return respond;
    }

    public static int parseSetRepeatingPeriodRespond(String userMessage, String chatId){
        int respond;
        try{
            respond = Integer.parseInt(userMessage);
            if (respond > NoteAdder.remindPeriods.length - 1){
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e){
            LOGGER.info(chatId + ": Wrong format setting repeating period");
            throw new IllegalArgumentException();
        }
        switch (respond){
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 7;
            case 3:
                return 30;
            default:
                throw new IllegalArgumentException();
        }
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

    public static boolean parseRespondToOfferRespond(String userMessage, String chatId){
        int respond;
        try{
            respond = Integer.parseInt(userMessage);
            if (respond > 1){
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e){
            LOGGER.info(chatId + ": Wrong format in responding to offer");
            throw new IllegalArgumentException();
        }
        return respond == 0;
    }
}
