import java.util.*;

public class NoteMaker {
    public final SortedSet<Note> notes; //Все заметки
    private UserIO userIO;
    public NotePrinter notePrinter;
    //public DateTimeParser dateTimeParser;
    //private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    //private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public static Map<String, StateHolder> userStates;

    public NoteMaker(UserIO userIO, int notePrinterPeriodInSeconds) {
        userStates = new HashMap<String, StateHolder>();
        this.userIO = userIO;
        notes = new TreeSet<>(Comparator.comparing(Note::getRemindDate));
        notePrinter = new NotePrinter(userIO, notes);
        //dateTimeParser = new DateTimeParser(userIO);
        Timer timer = new Timer();
        timer.schedule(notePrinter, 1000 * notePrinterPeriodInSeconds, 1000 * notePrinterPeriodInSeconds);
    }

    public void addNote(String command, String chatId) {
        // Спрашивает у пользователя String noteText, LocalDateTime remindDate, LocalDateTime eventDate
        // Добавляет новую заметку в лист заметок.
        // Вызывает checkNotesToPrint.
        // Показывает результат операции (напр. "Заметка установлена на *дата*")

        userStates.get(chatId).currentState = UserStates.ADDING;
        userStates.get(chatId).updateCurrentDate();
        userStates.get(chatId).addingState = AddingStates.SET_TEXT;
        userIO.showMessage("Write your note", chatId);
    }

    public void removeNote(String command, String chatId) { //TODO: userId
        throw new UnsupportedOperationException();
        //TODO: Удаляет заметку и напоминания о ней. Показывает пользователю список всех напоминаний (если больше 10, то выводит страницами.)
        // предлагает выбрать номер заметки в списке и удалить ее.
    }

    public void showUserNotes(String command, String chatId) {
        // Позволяет вывесли ближайшие 10 событий, все события, события на сегодня. Всю инфу спрашивает у пользователя.


//        List<Note> userNotes = new ArrayList<>();
//        for (Note note : notes) {
//            if (note.getChatId() == chatId) { //TODO: chatId
//                userNotes.add(note);
//            }
//        }
        userStates.get(chatId).currentState = UserStates.SHOWING;
        userIO.showOnClickButton("Chose the period", new String[]{"for today", "10 upcoming", "all"}, chatId);
    }
}
