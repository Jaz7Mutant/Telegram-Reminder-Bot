package com.jaz7.reminder;

import com.jaz7.inputOutput.UserIO;

import java.util.*;
import java.util.logging.Logger;

public class Reminder {
    public final SortedSet<Note> notes; //Все заметки
    private UserIO userIO;
    public NotePrinter notePrinter;
    public DateTimeParser dateTimeParser;
    public static Map<String, NoteKeeper> userStates;
    private static final Logger LOGGER = Logger.getLogger(Reminder.class.getSimpleName());

    public Reminder(UserIO userIO, int notePrinterPeriodInSeconds, NoteSerializer noteSerializer) {
        userStates = new HashMap<String, NoteKeeper>();
        this.userIO = userIO;
        notes = Collections.synchronizedSortedSet(noteSerializer.deserializeNotes());
        notePrinter = new NotePrinter(userIO, notes, noteSerializer);
        dateTimeParser = new DateTimeParser(userIO);
        LOGGER.info("Reminder has been created");
        Timer timer = new Timer();
        timer.schedule(notePrinter, 1000 * notePrinterPeriodInSeconds, 1000 * notePrinterPeriodInSeconds);
        LOGGER.info("NotePrinter has been started");
    }

    public void addMeeting(String command, String chatId){
        LOGGER.info(chatId + ": Switch to adding meeting state");
        userStates.get(chatId).currentState = UserState.ADDING;
        DateTimeParser.updateCurrentDate();
        userStates.get(chatId).addingState = AddingState.SET_MEETING;
        userIO.showMessage("Write your note", chatId);
    }

    public void joinMeeting(String command, String chatId){
        LOGGER.info(chatId + ": Switch to joining to meeting");
        userStates.get(chatId).currentState = UserState.JOINING;
        userIO.showMessage("Send the invite token", chatId);
    }

    public void addNote(String command, String chatId) {
        // Спрашивает у пользователя String noteText, LocalDateTime remindDate, LocalDateTime eventDate
        // Добавляет новую заметку в лист заметок.
        // Вызывает checkNotesToPrint.
        // Показывает результат операции (напр. "Заметка установлена на *дата*")

        LOGGER.info(chatId + ": Switch to adding note state");
        userStates.get(chatId).currentState = UserState.ADDING;
        DateTimeParser.updateCurrentDate();
        userStates.get(chatId).addingState = AddingState.SET_TEXT;
        userIO.showMessage("Write your note", chatId);
    }

    public void removeNote(String command, String chatId) {
        // Удаляет заметку. Показывает пользователю список всех напоминаний предлагает выбрать
        // номер заметки в списке и удалить ее.

        LOGGER.info(chatId + ": Switch to removing note state");
        userStates.get(chatId).currentState = UserState.REMOVING;
        NotePrinter.showUsersNotes("2", chatId, this, UserState.REMOVING);
        if (NotePrinter.getUserNotes(this, chatId).size() <= 0) {
            userStates.get(chatId).currentState = UserState.IDLE;
        }
        else{
            userIO.showMessage("Which note do you want to delete?", chatId);
        }
    }

    public void showUserNotes(String command, String chatId) {
        // Позволяет вывесли ближайшие 10 событий, все события, события на сегодня. Всю инфу спрашивает у пользователя.

        LOGGER.info(chatId + ": Switch to showing notes state");
        userStates.get(chatId).currentState = UserState.SHOWING;
        userIO.showOnClickButton("Chose the period", new String[]{"for today", "10 upcoming", "all"}, chatId);
    }
}
