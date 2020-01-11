package com.jaz7.reminder;

import com.jaz7.bot.BotOptions;
import com.jaz7.inputOutput.UserIO;
import com.jaz7.serializer.NoteSerializer;
import com.jaz7.user.UserState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class NotePrinter extends TimerTask {
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
            BotOptions.botOptions.get("DateTimePattern"));
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(
            BotOptions.botOptions.get("TimePattern"));
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
        // Смотрит на ближайшие события и если нужно, выводит напоминание о них, после чего удаляет
        if (notes.isEmpty()) {
            LOGGER.info("No notes to print");
            return;
        }
        LOGGER.info("Printing notes...");
        LocalDateTime currentTime = LocalDateTime.now();
        boolean isRemovedNotes = false;
        Note currentNote = notes.first();
        while (currentNote.getRemindDate().minusSeconds(30).isBefore(currentTime)) {
            {
                printNote(currentNote);
                LOGGER.info("Deleting remind...");
                if (currentNote.getEventDate().minusSeconds(30).isBefore(currentTime)) {
                    LOGGER.info("Deleting note...");
                    notes.remove(currentNote);
                    isRemovedNotes = true;
                }
                else {
                    currentNote.deleteBeforehandRemind(notes);
                }
                noteSerializer.serializeNotes(notes);
                if (notes.isEmpty()) {
                    return;
                }
                currentNote = notes.first();
            }
        }
        LOGGER.info("Printing notes has been done");
        if (isRemovedNotes) {
            noteSerializer.serializeNotes(notes); // TODO Можно убрать для ускорения работы
        }
    }

    public static UserState showUsersNotes(String command, String chatId, Reminder reminder, UserState currentState) {
        int respond;
        List<Note> userNotes = reminder.getUserNotes(chatId);
        if (userNotes.size() == 0) {
            userIO.showMessage(BotOptions.botAnswers.get("NoNotes"), chatId);
            return UserState.IDLE;
        }
        String[] values = new String[]{
                BotOptions.botAnswers.get("ForToday"),
                BotOptions.botAnswers.get("10Upcoming"),
                BotOptions.botAnswers.get("All")
        };
        respond = Arrays.asList(values).indexOf(command);
        if (respond == -1) {
            userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), chatId);
            return currentState;
        }

        switch (respond) {
            case 0:
                printTodayNotes(userNotes, chatId);
                break;
            case 1:
                printNotes(userNotes, chatId, Math.min(userNotes.size(), 10));
                break;
            case 2:
                printNotes(userNotes, chatId, userNotes.size());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + respond);
        }
        return UserState.IDLE;
    }

    private void printNote(Note note) {
        userIO.showMessage(BotOptions.botAnswers.get("Remind") + note.getText(), note.getChatId());
    }

    private static void printTodayNotes(List<Note> userNotes, String chatId) {
        LocalDateTime currentDate = LocalDateTime.now();
        List<String> todayNotes = userNotes.stream()
                .takeWhile(x -> x.getEventDate().getDayOfYear() == currentDate.getDayOfYear() &&
                        x.getEventDate().getYear() == currentDate.getYear())
                .map(x -> getFormattedNote(x, timeFormatter))
                .collect(Collectors.toList());
        userIO.showList(BotOptions.botAnswers.get("ShowTodayNotes"), todayNotes.toArray(String[]::new), chatId);
    }

    private static void printNotes(List<Note> userNotes, String chatId, int count) {
        List<String> formattedUserNotes = userNotes.stream()
                .limit(count)
                .map(x -> getFormattedNote(x, dateTimeFormatter))
                .collect(Collectors.toList());
        userIO.showList(BotOptions.botAnswers.get("ShowAllNotes"), formattedUserNotes.toArray(String[]::new), chatId);
    }

    private static String getFormattedNote(Note note, DateTimeFormatter formatter) {
        return String.format("%s %s%s", note.getEventDate().format(formatter),
                note.getText().length() >= 10 ? note.getText().substring(0, 10) : note.getText(),
                note.isRepeatable() ? String.format("... every %s day(s)", note.getRemindPeriod()) : "");

    }
}