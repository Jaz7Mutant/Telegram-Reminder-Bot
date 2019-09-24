import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NoteMaker {
    private static List<Note> notes; //Все заметки
    private static Map<LocalDateTime, Note> currentDayNotes; //События, которые произойдут сегодня. Ключ -- дата и время напоминания
    private static Timer timer = new Timer();

    public NoteMaker() {
        //TODO: конструктор или просто все проинициализировать
    }

    public static String addNote() {
        Scanner in = new Scanner(System.in);
        System.out.println("Write your note");
        String noteText = in.nextLine();
        System.out.println("When ? Write the date in yyyy-MM-dd: HH:mm:ss format");
        String stringEventDate = in.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd: HH:mm:ss");
        LocalDateTime eventDate = LocalDateTime.parse(stringEventDate, formatter);
        System.out.println("When ? Write the date in yyyy-MM-dd: HH:mm:ss format");
        String stringRemindDate = in.nextLine();
        LocalDateTime remindDate = LocalDateTime.parse(stringRemindDate, formatter);
        Note note = new Note(noteText, eventDate, remindDate);
        notes.add(note);
        updateCurrentDayNotes();
        return "You have a new note {0} on {1}, remind on {2}".format(noteText, eventDate, remindDate);
        //TODO: Спрашивает у пользователя String noteText, LocalDateTime noticeDate, LocalDateTime eventDate
        // Добавляет новую заметку в лист заметок.
        // Вызывает updateCurrentDayNotes.
        // Возвращает результат операции (напр. "Заметка установлена на *дата*")
    }

    public static String removeNote() {
        throw new UnsupportedOperationException();
        //TODO: Удаляет заметку и напоминания о ней. Показывает пользователю список всех напоминаний (если больше 10, то выводит страницами.)
        // предлагает выбрать номер заметки в списке и удалить ее.
    }

    public static String showAllNotes() {
        throw new UnsupportedOperationException();
        // TODO: Позволяет вывесли ближайшие 10 событий, все события, события на сегодня. Всю инфу спрашивает у пользователя.
        //  если больше 10, то выводит страницами.
        //  return null
    }

    private static void updateCurrentDayNotes() {
        //TODO: Убирает прошедшие события (из всех и из сегодняшних), загружает новые, проходя по всем из notes.
        // Добавляет в отложенный запуск событие.

        LocalDateTime today = LocalDateTime.now();
        for (Note note:notes) {
            if (note.getEventDate().isBefore(today)){
                notes.remove(note);
            }
            if (note.getRemindDate().toLocalDate().compareTo(today.toLocalDate()) == 0){
                currentDayNotes.put(note.getRemindDate(),note); //Добавляет заметку в буфер заметок с напоминанием сегодня
            }                                                                           // TODO: ReFUCKtor this shit
            if (note.getEventDate().toLocalDate().compareTo(today.toLocalDate()) == 0){
                currentDayNotes.put(note.getEventDate(),note);
            }
        }

        for (Note note:currentDayNotes.values()){  //Создает напоминания на сегодня -- функции, показывающие тект.
            timer.schedule(new NotePrinter(note.getText()),Date.from(note.getRemindDate().atZone(ZoneId.systemDefault()).toInstant()));
        }
        currentDayNotes.clear();
    }
}
