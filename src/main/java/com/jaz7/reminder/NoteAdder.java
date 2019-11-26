package com.jaz7.reminder;

import com.jaz7.bot.BotOptions;
import com.jaz7.inputOutput.UserIO;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.logging.Logger;

public class NoteAdder {
    public AddingState addingState;
    public boolean isMeeting;
    public String[] daysInCurrentMonth;
    private UserIO userIO;
    private NoteSerializer noteSerializer;
    private Reminder reminder;
    private String chatId;
    private NoteKeeper noteKeeper;
    private String newNoteText;
    private Calendar newRawDate;
    private Calendar newRawRemindDate;
    private LocalDateTime newNoteDate;
    private LocalDateTime newNoteRemindDate;
    private long newNoteRemindPeriod;

    private static final Logger LOGGER = Logger.getLogger(NoteAdder.class.getSimpleName());

    public NoteAdder(UserIO userIO, NoteSerializer noteSerializer, Reminder reminder, String chatId, NoteKeeper noteKeeper){
        this.userIO = userIO;
        this.noteSerializer = noteSerializer;
        this.chatId = chatId;
        this.reminder = reminder;
        this.noteKeeper = noteKeeper;
        newRawDate = Calendar.getInstance();
        newRawRemindDate = Calendar.getInstance();
    }

    public void doNextAddingStep(String userMessage) {
        switch (addingState) {
            case SET_MEETING:
                isMeeting = true; // Продолжить добавление заметки, поэтому нет break
            case SET_TEXT:
                LOGGER.info(chatId + ": Setting text");
                if (userMessage == null) {
                    userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), chatId);
                    userIO.showMessage(BotOptions.botAnswers.get("WriteNote"), chatId);
                    return;
                } else {
                    newNoteText = userMessage;
                    LOGGER.info(chatId + ": Text has been set");
                    userIO.showMessage(BotOptions.botAnswers.get("When"), chatId);
                    userIO.showOnClickButton(BotOptions.botAnswers.get("ChooseYear"), DateTimeParser.years, chatId);
                    addingState = AddingState.SET_YEAR;
                    return;
                }
            case SET_YEAR:
                LOGGER.info(chatId + ": Setting year");
                settingYear(userMessage, newRawDate);
                return;

            case SET_REMIND_YEAR:
                LOGGER.info(chatId + ": Setting remind year");
                settingYear(userMessage, newRawRemindDate);
                return;

            case SET_MONTH:
                LOGGER.info(chatId + ": Setting month");
                settingMonth(userMessage, newRawDate);
                return;

            case SET_REMIND_MONTH:
                LOGGER.info(chatId + ": Setting remind month");
                settingMonth(userMessage, newRawRemindDate);
                return;

            case SET_DAY:
                LOGGER.info(chatId + ": Setting day");
                settingDay(userMessage, newRawDate);
                return;

            case SET_REMIND_DAY:
                LOGGER.info(chatId + ": Setting remind day");
                settingDay(userMessage, newRawRemindDate);
                return;

            case SET_TIME:
                LOGGER.info(chatId + ": Setting time");
                settingTime(userMessage, newRawDate);
                return;

            case SET_REMIND_TIME:
                LOGGER.info(chatId + ": Setting remind time");
                settingTime(userMessage, newRawRemindDate);
                return;

            case SET_REMIND:
                LOGGER.info(chatId + ": Setting remind by offset");
                setRemind(userMessage);
        }
    }

    private void settingYear(String userMessage, Calendar rawDate){
        int respond;
        try{
            respond = RespondParser.parseSetYearRespond(userMessage, chatId);
            if (respond == 0){
                userIO.showMessage(BotOptions.botAnswers.get("SetYear"), chatId);
                return;
            }
        }
        catch (IllegalArgumentException e){
            userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), chatId);
            return;
        }
        addingState = DateTimeParser.setYear(rawDate, respond, chatId, addingState);
    }

    private void settingMonth(String userMessage, Calendar rawDate){
        int respond;
        try{
            respond = RespondParser.parseSetMonthRespond(userMessage, chatId) - 1;
        }
        catch (IllegalArgumentException e){
            userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), chatId);
            return;
        }
        addingState = DateTimeParser.setMonth(rawDate, respond, chatId, addingState);
    }

    private void settingDay(String userMessage, Calendar rawDate){
        int respond;
        try {
            respond = RespondParser.parseSetDayRespond(userMessage, chatId, daysInCurrentMonth) + 1;
        }
        catch (IllegalArgumentException e){
            userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), chatId);
            return;
        }
        addingState = DateTimeParser.setDay(rawDate, respond, chatId, addingState);
    }

    private void settingTime(String userMessage, Calendar rawDate){
        LocalTime time;
        try {
            time = RespondParser.parseSerTimeRespond(userMessage,chatId);
        }
        catch (IllegalArgumentException e){
            userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), chatId);
            return;
        }
        addingState = DateTimeParser.setTime(rawDate, time,chatId,addingState);
        if (addingState == AddingState.SET_REMIND) {
            LOGGER.info(chatId + ": Setting remind type");
            if (addingState == AddingState.SET_REMIND) {
                newNoteDate = LocalDateTime.ofInstant(newRawDate.toInstant(), newRawDate.getTimeZone().toZoneId());
                userIO.showOnClickButton(BotOptions.botAnswers.get("SetRemind"), new String[]{
                        BotOptions.botAnswers.get("NoRemind"),
                        BotOptions.botAnswers.get("HourBefore"),
                        BotOptions.botAnswers.get("DayBefore"),
                        BotOptions.botAnswers.get("WeekBefore"),
                        BotOptions.botAnswers.get("SetDate"),
                        BotOptions.botAnswers.get("Every day"),
                        BotOptions.botAnswers.get("Every week"),
                        BotOptions.botAnswers.get("Every month"),}, chatId);
            } else if (addingState == AddingState.IDLE) {
                finishAddNote();
            }
        }
        if (addingState == AddingState.IDLE){
            newNoteRemindDate = LocalDateTime.ofInstant(newRawRemindDate.toInstant(), newRawRemindDate.getTimeZone().toZoneId());
            finishAddNote();
        }
    }

    private void setRemind(String userMessage){
        LOGGER.info(chatId + ": Setting remind..");
        int respond;
        try{
            respond = RespondParser.parseSetRemindRespond(userMessage, chatId);
        }
        catch (IllegalArgumentException e){
            userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), chatId);
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
                userIO.showOnClickButton(BotOptions.botAnswers.get("ChooseYear"), DateTimeParser.years, chatId);
                addingState = AddingState.SET_REMIND_YEAR;
                return;
            case 5:
                newNoteRemindPeriod = 1;
                newNoteRemindDate = newNoteDate;
                break;
            case 6:
                newNoteRemindPeriod = 7;
                newNoteRemindDate = newNoteDate;
                break;
            case 7:
                newNoteRemindPeriod = 30;
                newNoteRemindDate = newNoteDate;
                break;
            default:
                throw new IllegalArgumentException();
        }
        finishAddNote();
    }

    private void finishAddNote(){
        LOGGER.info(chatId + ": Adding new note...");
        synchronized (reminder.notes) {
            Note newNote;
            if (newNoteRemindPeriod != 0){
                newNote = new Note(chatId, newNoteText, newNoteDate, newNoteRemindDate, true , newNoteRemindPeriod, null);
            }
            else {
                newNote = new Note(chatId, newNoteText, newNoteDate, newNoteRemindDate, false, 0, null);
            }

            if (isMeeting) {
                LOGGER.info(chatId + ": Setting meeting token");
                newNote.setToken();
            }
            reminder.notes.add(newNote);
            reminder.notePrinter.run();
            addingState = AddingState.IDLE;
            noteKeeper.currentState = UserState.IDLE;
            int stringLimit = 20;
            if (newNoteText.length() < 20) {
                stringLimit = newNoteText.length();
            }
            if (isMeeting){
                userIO.showMessage(BotOptions.botAnswers.get("NewMeeting")
                        + newNoteText.substring(0, stringLimit)
                        + BotOptions.botAnswers.get("WithRemind")
                        + newNoteRemindDate.format(NotePrinter.dateTimeFormatter), chatId);
                userIO.showMessage(BotOptions.botAnswers.get("YourToken"), chatId);
                userIO.showMessage(newNote.getToken(), chatId);
                userIO.showMessage(BotOptions.botAnswers.get("ShareToken"), chatId);
            }
            else {
                userIO.showMessage(BotOptions.botAnswers.get("NewNote")
                        + newNoteText.substring(0, stringLimit)
                        + BotOptions.botAnswers.get("WithRemind")
                        + newNoteRemindDate.format(NotePrinter.dateTimeFormatter), chatId);
            }
            noteSerializer.serializeNotes(reminder.notes);
            if (newNoteRemindPeriod != 0) {
                userIO.showMessage(BotOptions.botAnswers.get("RemindPeriodInDays") + newNoteRemindPeriod, chatId);
            }
        }
        LOGGER.info(chatId + ": New note has been added");
        isMeeting = false;
        newNoteRemindPeriod = 0;
    }
}
