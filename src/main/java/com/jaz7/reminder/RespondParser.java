package com.jaz7.reminder;

import com.jaz7.inputOutput.UserIO;

import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static com.jaz7.reminder.DateTimeParser.currentDate;
import static com.jaz7.reminder.DateTimeParser.years;

public class RespondParser {
    public static UserIO userIO;
    private static final Logger LOGGER = Logger.getLogger(RespondParser.class.getSimpleName());

    public RespondParser(UserIO userIO) {
        LOGGER.info("Initializing RespondParser");
        this.userIO = userIO;
    }

    public static int parseSetYearRespond(String userMessage, String chatId) {
        int respond;
        if (userMessage.equals(years[years.length - 1])) {
            LOGGER.info(String.format("%s: Setting year manually", chatId));
            return 0;
        }
        try {
            respond = Integer.parseInt(userMessage);
        } catch (NumberFormatException e) {
            LOGGER.info(String.format("%s: Wrong year format", chatId));
            return -1;
        }
        if (respond < currentDate.getYear() || respond > 2035) {
            LOGGER.info(String.format("%s: Illegal year", chatId));
            return -1;
        }
        return respond;
    }

    public static int parseSetMonthRespond(String userMessage, String chatId) {
        int respond;
        try {
            respond = Month.valueOf(userMessage.toUpperCase()).getValue();
        } catch (IllegalArgumentException e) {
            try {
                respond = Integer.parseInt(userMessage);
                if (respond > 12 || respond < 1) {
                    return -1;
                }
            } catch (NumberFormatException ex) {
                LOGGER.info(String.format("%s: Wrong month format", chatId));
                return -1;
            }
        }
        return respond;
    }

    public static int parseSetDayRespond(String userMessage, String chatId, String[] days) {
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
            if (respond > days.length || respond < 1) {
                return -1;
            }
        } catch (NumberFormatException e) {
            LOGGER.info(String.format("%s: Wrong day format", chatId));
            return -1;
        }
        return respond;
    }

    public static LocalTime parseSetTimeRespond(String userMessage, String chatId) {
        LocalTime time;
        try {
            time = LocalTime.parse(userMessage);
        } catch (Exception e) {
            LOGGER.info(String.format("%s: Wrong time format", chatId));
            return null;
        }
        return time;
    }

    public static int parseSetRemindRespond(String userMessage, String chatId) {
        int respond = Arrays.asList(NoteAdder.remindTypes).indexOf(userMessage);
        if (respond == -1)
            LOGGER.info(String.format("%s: Wrong format in setting remind", chatId));
        return respond;
    }

    public static int parseSetRepeatingPeriodRespond(String userMessage, String chatId) {
        int respond = Arrays.asList(NoteAdder.remindTypes).indexOf(userMessage);
        if (respond == -1) {
            LOGGER.info(String.format("%s: Wrong format setting repeating period", chatId));
            return -1;
        }
        switch (respond) {
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

    public static int parseRemoveNoteRespond(String userMessage, List<Note> userNotes, String chatId) {
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
            if (respond > userNotes.size() || respond <= 0) {
                return -1;
            }
        } catch (NumberFormatException e) {
            LOGGER.info(String.format("%s: Wrong format in removing notes", chatId));
            return -1;
        }
        return respond;
    }

    public static boolean parseRespondToOfferRespond(String userMessage, String chatId) {
        int respond;
        try {
            respond = Integer.parseInt(userMessage);
            if (respond > 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            LOGGER.info(String.format("%s: Wrong format in responding to offer", chatId));
            throw new IllegalArgumentException();
        }
        return respond == 0;
    }
}
