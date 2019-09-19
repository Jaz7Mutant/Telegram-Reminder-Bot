import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class NoteMaker {
    private static List<Note> notes; //Все заметки
    private static Map<LocalDateTime, Note> currentDayNotes; //События, которые произойдут сегодня. Ключ -- дата и время напоминания

    public NoteMaker() {
        //TODO: конструктор или просто все проинициализировать
    }

    public static String addNote() {
        throw new UnsupportedOperationException();
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

    private static void printNotice(Note note) {
        throw new UnsupportedOperationException();
        // Чисто утилитарная функция. В ручную не вызывается.
        //TODO
    }

    private static void updateCurrentDayNotes() {
        throw new UnsupportedOperationException();
        //TODO: Убирает прошедшие события (из всех и из сегодняшних), загружает новые, проходя по всем из notes.
        // Добавляет в отложенный запуск событие.
        //Timer timer = new Timer();
        //timer.schedule(MyTimeTask extends TimerTask);
    }
}
