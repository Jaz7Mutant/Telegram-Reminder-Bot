package com.jaz7.reminder;

import bot.BotOptions;
import com.jaz7.inputOutput.UserIO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotePrinter extends TimerTask {
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(BotOptions.botOptions.get("DateTimePattern"));
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(BotOptions.botOptions.get("TimePattern"));
    private static UserIO userIO;
    private static NoteSerializer noteSerializer;
    private final SortedSet<Note> notes;
    private static final Logger LOGGER = Logger.getLogger(NotePrinter.class.getSimpleName());

    public NotePrinter(UserIO userIO, SortedSet<Note> notes, NoteSerializer noteSerializer) {
        NotePrinter.userIO = userIO;
        this.notes = notes;
        NotePrinter.noteSerializer = noteSerializer;
        LOGGER.info("NotePrinter has been created");
    }

    @Override
    public void run() {
        LOGGER.info("Find notes to print...");
        // Смотрит на ближайшее событие и если нужно, выводит напоминание о нем, после чего удаляет
        if (notes.isEmpty()) {
            LOGGER.info("No notes to print");
            return;
        }
        // todo lock
        //  выводить список, а не одну заметку
        LOGGER.info("Printing notes...");
        LocalDateTime currentTime = LocalDateTime.now();
        synchronized (notes) {
            Note currentNote = notes.first();
            while (currentNote.getRemindDate().minusSeconds(30).isBefore(currentTime)) {
                {
                    try {
                        printNote(currentNote);
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Error sending message" + e.getMessage(), e); // todo
                    }
                    LOGGER.info("Deleting remind...");
                    currentNote.deleteBeforehandRemind();
                    if (currentNote.getEventDate().minusSeconds(30).isBefore(currentTime)) {
                        LOGGER.info("Deleting note...");
                        notes.remove(currentNote);
                    }
                    noteSerializer.serializeNotes(notes);
                    if (notes.isEmpty()) {
                        return;
                    }
                    currentNote = notes.first();
                }
            }
        }
        LOGGER.info("Printing notes has been done");
    }

    public static List<Note> getUserNotes(Reminder reminder, String chatId) {
        List<Note> userNotes = new ArrayList<>();
        synchronized (reminder.notes) {
            for (Note note : reminder.notes) {
                if (note.getChatId().equals(chatId)) {
                    userNotes.add(note);
                }
            }
            return userNotes;
        }
    }

    public static UserState showUsersNotes(String command, String chatId, Reminder reminder, UserState currentState){
        int respond;
        List<Note> userNotes = getUserNotes(reminder, chatId);
        if (userNotes.size() == 0){
            userIO.showMessage(BotOptions.botAnswers.get("NoNotes"), chatId);
            return UserState.IDLE;
        }
        try {
            respond = Integer.parseInt(command);
        } catch (NumberFormatException e) {
            userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), chatId);
            return currentState;
        }

        switch (respond) {
            case 0:
                printTodayNotes(userNotes, chatId);
                break;
            case 1:
                printTenUpcomingNotes(userNotes, chatId);
                break;
            case 2:
                printAllNotes(userNotes, chatId);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + respond);
        }
        return UserState.IDLE;
    }

    private void printNote(Note note) {
        userIO.showMessage(BotOptions.botAnswers.get("Remind") + note.getText(),note.getChatId());
        //todo:
    }

    private static void printTodayNotes(List<Note> userNotes, String chatId) {
        LocalDateTime currentDate = LocalDateTime.now();
        List<String> todayNotes = new ArrayList<>();
        for (Note note : userNotes) {
            if (note.getEventDate().getDayOfYear() == currentDate.getDayOfYear()
                    && note.getEventDate().getYear() == currentDate.getYear()) {
                if (note.getText().length() >=10) {
                    todayNotes.add(note.getEventDate().format(timeFormatter) + " " + note.getText().substring(0, 10));
                }
                else{
                    todayNotes.add(note.getEventDate().format(timeFormatter) + " " + note.getText());
                }
            }
        }
        userIO.showList(BotOptions.botAnswers.get("ShowTodayNotes"), todayNotes.toArray(new String[0]), chatId);
    }

    private static void printTenUpcomingNotes(List<Note> userNotes, String chatId) {
        if (userNotes.size() > 10) { // Если меньше 10, то выводятся все заметки
            String[] upcomingNotes = new String[10];
            Note currentNote;
            for (int i = 0; i < 10; i++) {
                currentNote = userNotes.get(i);
                if (currentNote.getText().length() >= 10){
                    upcomingNotes[i] = currentNote.getEventDate().format(dateTimeFormatter) + " "
                            + currentNote.getText().substring(0, 10);
                }
                else{
                    upcomingNotes[i] = currentNote.getEventDate().format(dateTimeFormatter) + " "
                            + currentNote.getText();
                }
            }
            userIO.showList(BotOptions.botAnswers.get("Show10Upcoming"), upcomingNotes, chatId);
        } else printAllNotes(userNotes, chatId);
    }

    private static void printAllNotes(List<Note> userNotes, String chatId) {
        String[] formattedUserNotes = new String[userNotes.size()];
        Note currentNote;
        for (int i = 0; i < userNotes.size(); i++) {
            currentNote = userNotes.get(i);
            if (currentNote.getText().length() >= 10){
                formattedUserNotes[i] = currentNote.getEventDate().format(dateTimeFormatter) + " "
                        + currentNote.getText().substring(0, 10);
            }
            else{
                formattedUserNotes[i] = currentNote.getEventDate().format(dateTimeFormatter) + " "
                        + currentNote.getText();
            }
        }
        userIO.showList(BotOptions.botAnswers.get("ShowAllNotes"), formattedUserNotes, chatId);
    }
}