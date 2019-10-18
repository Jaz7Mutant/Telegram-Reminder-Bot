import java.util.*;

public class NoteMaker {
    public final SortedSet<Note> notes; //Все заметки
    private UserIO userIO;
    public NotePrinter notePrinter;
    //private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    //private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public static Map<Long, StateHolder> userStates;

    public NoteMaker(UserIO userIO, int notePrinterPeriodInSeconds) {
        userStates = new HashMap<Long, StateHolder>();
        this.userIO = userIO;
        notes = new TreeSet<>(Comparator.comparing(Note::getRemindDate));
        notePrinter = new NotePrinter(userIO, notes);
        Timer timer = new Timer();
        timer.schedule(notePrinter, 1000 * notePrinterPeriodInSeconds, 1000 * notePrinterPeriodInSeconds);
    }

    public void addNote(String chatId) {
        // Спрашивает у пользователя String noteText, LocalDateTime remindDate, LocalDateTime eventDate
        // Добавляет новую заметку в лист заметок.
        // Вызывает checkNotesToPrint.
        // Показывает результат операции (напр. "Заметка установлена на *дата*")

        userStates.get(Long.parseLong(chatId)).currentState = UserStates.ADDING;


//        userIO.showMessage("Write your note", Long.parseLong(userId));
//        userStates.get(Long.parseLong(userId)).currentState = UserStates.ADDING;
//
//
//        DateTimeParser dateTimeParser = new DateTimeParser();
//        String noteText = userIO.getUserText("Write your note", 0);
//        userIO.showMessage("When will it happen?", 0);
//        LocalDateTime eventDate = dateTimeParser.getDateTime(userIO);
//
//        userIO.showMessage("Set the date of remind", 0);
//        LocalDateTime remindDate = dateTimeParser.getDateTimeWithOffset(userIO, eventDate);
//
//        notes.add(new Note(
//                //userId,
//                "",
//                noteText, eventDate, remindDate)); //TODO: usedID
//        notePrinter.run();
//        int stringLimit = 20;
//        if (noteText.length() < 20){
//            stringLimit = noteText.length();
//        }
//        userIO.showMessage("You have a new note \""
//                + noteText.substring(0, stringLimit) + "...\" with remind on " + remindDate.format(dateTimeFormatter), 0);
    }

    public void removeNote(String chatId) { //TODO: userId
        throw new UnsupportedOperationException();
        //TODO: Удаляет заметку и напоминания о ней. Показывает пользователю список всех напоминаний (если больше 10, то выводит страницами.)
        // предлагает выбрать номер заметки в списке и удалить ее.
    }

    public void showUserNotes(String chatId) {
        // Позволяет вывесли ближайшие 10 событий, все события, события на сегодня. Всю инфу спрашивает у пользователя.



//        List<Note> userNotes = new ArrayList<>();
//        for (Note note : notes) {
//            if (note.getChatId() == chatId) { //TODO: chatId
//                userNotes.add(note);
//            }
//        }
        userStates.get(Long.parseLong(chatId)).currentState = UserStates.SHOWING;
        userIO.getOnClickButton(new String[]{"for today", "10 upcoming", "all"}, chatId);
//        switch (respond) {
//            case 0:
//                printTodayNotes(userNotes, chatId);
//                break;
//            case 1:
//                printTenUpcomingNotes(userNotes, chatId);
//                break;
//            case 2:
//                printAllNotes(userNotes, chatId);
//                break;
//            default:
//                throw new IllegalStateException("Unexpected value: " + respond);
//        }
    }

//    private void printTodayNotes(List<Note> userNotes, long chatId){
//        LocalDateTime currentDate = LocalDateTime.now();
//        List<String> todayNotes = new ArrayList<>();
//        for (Note note : userNotes) {
//            if (note.getEventDate().getDayOfYear() == currentDate.getDayOfYear()
//                    && note.getEventDate().getYear() == currentDate.getYear()) {
//                todayNotes.add(note.getEventDate().format(timeFormatter) + " " + note.getText().substring(0, 10));
//            }
//        }
//        userIO.showList("Today's notes:", todayNotes.toArray(new String[0]), chatId);
//    }
//
//    private void printTenUpcomingNotes(List<Note> userNotes, long chatId){
//        if (userNotes.size() > 10) { // Если меньше 10, то выводятся все заметки
//            String[] upcomingNotes = new String[10];
//            Note currentNote;
//            for (int i = 0; i < 10; i++) {
//                currentNote = userNotes.get(i);
//                upcomingNotes[i] = currentNote.getEventDate().format(dateTimeFormatter) + " "
//                        + currentNote.getText().substring(0, 10);
//            }
//            userIO.showList("10 upcoming notes:", upcomingNotes, chatId);
//        }
//        else printAllNotes(userNotes, chatId);
//    }
//
//    private void printAllNotes(List<Note> userNotes, long chatId){
//        String[] formattedUserNotes = new String[userNotes.size()];
//        Note currentNote;
//        for (int i = 0; i < userNotes.size(); i++){
//            currentNote = userNotes.get(i); // Разве так сложно было прикрутить индексацию к листу?????
//            formattedUserNotes[i] = currentNote.getEventDate().format(dateTimeFormatter) + " "
//                    + currentNote.getText().substring(0,10);
//        }
//        userIO.showList("Your notes:", formattedUserNotes, chatId);
//    }
}
