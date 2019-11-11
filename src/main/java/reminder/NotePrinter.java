package reminder;

import inputOutput.UserIO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TimerTask;

public class NotePrinter extends TimerTask {
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static UserIO userIO;
    private static NoteSerializer noteSerializer;
    private final SortedSet<Note> notes;

    public NotePrinter(UserIO userIO, SortedSet<Note> notes, NoteSerializer noteSerializer) {
        NotePrinter.userIO = userIO;
        this.notes = notes;
        NotePrinter.noteSerializer = noteSerializer;
    }

    @Override
    public void run() {
        // Смотрит на ближайшее событие и если нужно, выводит напоминание о нем, после чего удаляет
        if (notes.isEmpty()) {
            return;
        }
        // todo lock
        //  выводить список, а не одну заметку
        LocalDateTime currentTime = LocalDateTime.now();
        synchronized (notes) {
            Note firstNote = notes.first();
            if (firstNote.getRemindDate().isBefore(currentTime)) {
                try {
                    printNote(firstNote);
                } catch (Exception e) {
                    System.out.println(firstNote.getChatId() + "Wrong chat id");
                }

                firstNote.deleteBeforehandRemind();
                if (firstNote.getEventDate().isBefore(currentTime)) {
                    notes.remove(firstNote);
                }
                noteSerializer.serializeNotes(notes);
            }
        }
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
            userIO.showMessage("You have no notes", chatId);
            return UserState.IDLE;
        }
        try {
            respond = Integer.parseInt(command);
        } catch (NumberFormatException e) {
            userIO.showMessage("Wrong format", chatId);
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
        userIO.showMessage("Don't forget about: " + note.getText(),note.getChatId());
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
        userIO.showList("Today's notes:", todayNotes.toArray(new String[0]), chatId);
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
            userIO.showList("10 upcoming notes:", upcomingNotes, chatId);
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
        userIO.showList("Your notes:", formattedUserNotes, chatId);
    }
}
