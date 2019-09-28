import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class NoteMaker {
    private static SortedSet<Note> notes = new TreeSet<>(Comparator.comparing(Note::getRemindDate)); //Все заметки
    //private static Map<LocalDateTime, Note> currentDayNotes = new HashMap<LocalDateTime, Note>(); //События, которые произойдут сегодня. Ключ -- дата и время напоминания
    private static Timer timer = new Timer();
    private static UserIO userIO = new ConsoleIO();
    private static NotePrinter notePrinter = new NotePrinter(userIO, notes);

    public NoteMaker() {
        //TODO: конструктор или просто все проинициализировать
        timer.schedule(notePrinter,60000, 60000);
    }

    public static void addNote(String _s) {
        // Спрашивает у пользователя String noteText, LocalDateTime remindDate, LocalDateTime eventDate
        // Добавляет новую заметку в лист заметок.
        // Вызывает checkNotesToPrint.
        // Показывает результат операции (напр. "Заметка установлена на *дата*")

        DateTimeParser dateTimeParser = new DateTimeParser();
        String noteText = userIO.getUserText("Write your note");
        userIO.showMessage("When will it happen?");
        LocalDateTime eventDate = dateTimeParser.getDateTime(userIO);

        userIO.showMessage("Set the date of remind");
        LocalDateTime remindDate = dateTimeParser.getDateTimeWithOffset(userIO,eventDate);

        notes.add(new Note(noteText,eventDate,remindDate));
        notePrinter.run();
        userIO.showMessage("You have a new note {0}... with remind on {1}".format(noteText.substring(0, 20), remindDate));
    }

    public static void removeNote(String _s) {
        throw new UnsupportedOperationException();
        //TODO: Удаляет заметку и напоминания о ней. Показывает пользователю список всех напоминаний (если больше 10, то выводит страницами.)
        // предлагает выбрать номер заметки в списке и удалить ее.
    }

    public static void showAllNotes(String _s) {
        throw new UnsupportedOperationException();
        // TODO: Позволяет вывесли ближайшие 10 событий, все события, события на сегодня. Всю инфу спрашивает у пользователя.
        //  если больше 10, то выводит страницами.
        //  return null
    }

}
