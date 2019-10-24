package reminder;

import inputOutput.UserIO;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.Timer;

public class Reminder {
    public final SortedSet<Note> notes; //Все заметки
    private UserIO userIO;
    public NotePrinter notePrinter;
    public DateTimeParser dateTimeParser;
    public static Map<String, NoteKeeper> userStates;
    private JsonNoteSerializer noteSerializer;

    public Reminder(UserIO userIO, int notePrinterPeriodInSeconds) {
        userStates = new HashMap<String, NoteKeeper>();
        this.userIO = userIO;
        noteSerializer = new JsonNoteSerializer();
        notes = noteSerializer.deserializeNotes();
        //notes = new TreeSet<>(Comparator.comparing(reminder.Note::getRemindDate));
        notePrinter = new NotePrinter(userIO, notes, noteSerializer);
        dateTimeParser = new DateTimeParser(userIO);
        Timer timer = new Timer();
        timer.schedule(notePrinter, 1000 * notePrinterPeriodInSeconds, 1000 * notePrinterPeriodInSeconds);
    }

    public void addNote(String command, String chatId) {
        // Спрашивает у пользователя String noteText, LocalDateTime remindDate, LocalDateTime eventDate
        // Добавляет новую заметку в лист заметок.
        // Вызывает checkNotesToPrint.
        // Показывает результат операции (напр. "Заметка установлена на *дата*")

        userStates.get(chatId).currentState = UserStates.ADDING;
        DateTimeParser.updateCurrentDate();
        userStates.get(chatId).addingState = AddingStates.SET_TEXT;
        userIO.showMessage("Write your note", chatId);
    }

    public void removeNote(String command, String chatId) {
        // Удаляет заметку. Показывает пользователю список всех напоминаний предлагает выбрать
        // номер заметки в списке и удалить ее.

        userStates.get(chatId).currentState = UserStates.REMOVING;
        NotePrinter.showUsersNotes("2", chatId, this, UserStates.REMOVING);
        userIO.showMessage("Which note do you want to delete?", chatId);
    }

    public void showUserNotes(String command, String chatId) {
        // Позволяет вывесли ближайшие 10 событий, все события, события на сегодня. Всю инфу спрашивает у пользователя.

        userStates.get(chatId).currentState = UserStates.SHOWING;
        userIO.showOnClickButton("Chose the period", new String[]{"for today", "10 upcoming", "all"}, chatId);
    }
}