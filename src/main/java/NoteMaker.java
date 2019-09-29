import java.time.LocalDateTime;
import java.util.*;

public class NoteMaker {
    private SortedSet<Note> notes; //Все заметки
    private UserIO userIO;
    private NotePrinter notePrinter;

    public NoteMaker(UserIO userIO, int notePrinterPeriodInSeconds) {
        this.userIO = userIO;
        notes = new TreeSet<>(Comparator.comparing(Note::getRemindDate));
        notePrinter = new NotePrinter(userIO, notes);
        Timer timer = new Timer();
        timer.schedule(notePrinter, 1000 * notePrinterPeriodInSeconds, 1000 * notePrinterPeriodInSeconds);
    }

    public void addNote(String userId) {
        // Спрашивает у пользователя String noteText, LocalDateTime remindDate, LocalDateTime eventDate
        // Добавляет новую заметку в лист заметок.
        // Вызывает checkNotesToPrint.
        // Показывает результат операции (напр. "Заметка установлена на *дата*")

        DateTimeParser dateTimeParser = new DateTimeParser();
        String noteText = userIO.getUserText("Write your note");
        userIO.showMessage("When will it happen?");
        LocalDateTime eventDate = dateTimeParser.getDateTime(userIO);

        userIO.showMessage("Set the date of remind");
        LocalDateTime remindDate = dateTimeParser.getDateTimeWithOffset(userIO, eventDate);

        notes.add(new Note(userId, noteText, eventDate, remindDate)); //TODO: usedID
        notePrinter.run();
        userIO.showMessage("You have a new note {0}... with remind on {1}".format(noteText.substring(0, 20), remindDate));
    }

    public void removeNote(String userId) { //TODO: userId
        throw new UnsupportedOperationException();
        //TODO: Удаляет заметку и напоминания о ней. Показывает пользователю список всех напоминаний (если больше 10, то выводит страницами.)
        // предлагает выбрать номер заметки в списке и удалить ее.
    }

    public void showUserNotes(String userId) {
        // Позволяет вывесли ближайшие 10 событий, все события, события на сегодня. Всю инфу спрашивает у пользователя.
        // TODO: Раскидать в 3 приватных метода
        List<Note> userNotes = new ArrayList<>();
        for (Note note : notes) {
            if (note.getUserId().equals(userId)) { //TODO: userId
                userNotes.add(note);
            }
        }

        int respond = userIO.getOnClickButton(new String[]{"for today", "10 upcoming", "all"});
        switch (respond) {
            case 0:
                LocalDateTime currentDate = LocalDateTime.now();
                List<String> todayNotes = new ArrayList<>();
                for (Note note : userNotes) {
                    if (note.getEventDate().getDayOfYear() == currentDate.getDayOfYear()
                            && note.getEventDate().getYear() == currentDate.getYear()) {
                        todayNotes.add(note.getEventDate().toLocalTime() + note.getText().substring(0, 10));
                    }
                }
                userIO.showList("Today's notes:", todayNotes.toArray(new String[0]));
                break;
            case 1:
                if (userNotes.size() > 10) {
                    String[] upcomingNotes = new String[10];
                    Note currentNote;
                    for (int i = 0; i < 10; i++) {
                        currentNote = userNotes.get(i);
                        upcomingNotes[i] = currentNote.getEventDate().toLocalTime() + " "
                                + currentNote.getText().substring(0, 10);
                    }
                    userIO.showList("10 upcoming notes:", upcomingNotes);
                } // Если меньше 10, то выводятся все заметки, поэтому break нет
            case 2:
                String[] formattedUserNotes = new String[userNotes.size()];
                Note currentNote;
                for (int i = 0; i < userNotes.size(); i++){
                    currentNote = userNotes.get(i); // Разве так сложно было прикрутить индексацию к листу?????
                    formattedUserNotes[i] = currentNote.getEventDate().toString() + " "
                            + currentNote.getText().substring(0,10);
                }
                userIO.showList("Your notes:", formattedUserNotes);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + respond);
        }
    }
}
