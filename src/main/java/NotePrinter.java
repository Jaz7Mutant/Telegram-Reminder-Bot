import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TimerTask;

public class NotePrinter extends TimerTask {
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static UserIO userIO;
    private SortedSet<Note> notes;

    public NotePrinter(UserIO userIO, SortedSet<Note> notes) {
        this.userIO = userIO;
        this.notes = notes;
    }

    @Override
    public void run() {
        // Смотрит на ближайшее событие и если нужно, выводит напоминание о нем, после чего удаляет
        if (notes.isEmpty()) {
            return;
        }
        LocalDateTime currentTime = LocalDateTime.now();
        Note firstNote = notes.first();
        if (firstNote.getRemindDate().isBefore(currentTime)) {
            printNote(firstNote);
            firstNote.deleteBeforehandRemind();
            if (firstNote.getEventDate().isBefore(currentTime)) {
                notes.remove(firstNote);
            }
        }
    }

    public void printNote(Note note) {
        userIO.showMessage(note.getText(),note.getChatId());
        //todo:
    }

    public static void printTodayNotes(List<Note> userNotes, String chatId) {
        LocalDateTime currentDate = LocalDateTime.now();
        List<String> todayNotes = new ArrayList<>();
        for (Note note : userNotes) {
            if (note.getEventDate().getDayOfYear() == currentDate.getDayOfYear()
                    && note.getEventDate().getYear() == currentDate.getYear()) {
                todayNotes.add(note.getEventDate().format(timeFormatter) + " " + note.getText().substring(0, 10));
            }
        }
        userIO.showList("Today's notes:", todayNotes.toArray(new String[0]), chatId);
    }

    public static void printTenUpcomingNotes(List<Note> userNotes, String chatId) {
        if (userNotes.size() > 10) { // Если меньше 10, то выводятся все заметки
            String[] upcomingNotes = new String[10];
            Note currentNote;
            for (int i = 0; i < 10; i++) {
                currentNote = userNotes.get(i);
                upcomingNotes[i] = currentNote.getEventDate().format(dateTimeFormatter) + " "
                        + currentNote.getText().substring(0, 10);
            }
            userIO.showList("10 upcoming notes:", upcomingNotes, chatId);
        } else printAllNotes(userNotes, chatId);
    }

    public static void printAllNotes(List<Note> userNotes, String chatId) {
        String[] formattedUserNotes = new String[userNotes.size()];
        Note currentNote;
        for (int i = 0; i < userNotes.size(); i++) {
            currentNote = userNotes.get(i);
            formattedUserNotes[i] = currentNote.getEventDate().format(dateTimeFormatter) + " "
                    + currentNote.getText().substring(0, 10);
        }
        userIO.showList("Your notes:", formattedUserNotes, chatId);
    }
}
