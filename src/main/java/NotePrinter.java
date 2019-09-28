import java.time.LocalDateTime;
import java.util.SortedSet;
import java.util.TimerTask;

public class NotePrinter extends TimerTask {
    private UserIO userIO;
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
            if (firstNote.getEventDate().isBefore(currentTime)) {
                notes.remove(firstNote);
            }
        }
    }

    public void printNote(Note note) {
        //TODO:
    }
}
