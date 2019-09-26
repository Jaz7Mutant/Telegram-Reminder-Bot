import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class NoteMaker {
    private static List<Note> notes = new ArrayList<Note>(); //Все заметки
    private static Map<LocalDateTime, Note> currentDayNotes = new HashMap<LocalDateTime, Note>(); //События, которые произойдут сегодня. Ключ -- дата и время напоминания
    private static Timer timer = new Timer();
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd: HH:mm:ss");
    public NoteMaker() {
        //TODO: конструктор или просто все проинициализировать
    }

    public static LocalDateTime getDate(){
        Scanner in = new Scanner(System.in);
        int year = 0;
        int month = 0;
        int day = 0;
        int hour = 0;
        int minute = 0;
        int second = 0;
        boolean isDifferentDate = false;
        LocalDateTime date = LocalDateTime.of(1, 1,1,1,1,1);
        System.out.println("Choose the year: 1 - 2019, 2 - 2020, 3 - different ");
        switch (in.nextLine()){
            case "1": {
                year = 2019;
            }
            break;
            case "2": {
                year = 2020;
            }
            break;
            case "3": {
                isDifferentDate = true;
                System.out.println("Write the date in yyyy-MM-dd: HH:mm:ss format");
                String dateAnswer = in.nextLine();
                try{
                    date = LocalDateTime.parse(dateAnswer, formatter);
                }
                catch (DateTimeParseException e){
                    System.out.println("Incorrect input, try again");
                    return getDate();
                }

            }
            break;
            default: {
                System.out.println("Incorrect input, try again");
                return getDate();
            }
        }
        System.out.println("Choose the month: 1 - January, 2 - February, 3 - March, 4 - April, 5 - May, " +
                "6 - June, 7 - July, 8 - August, 9 - September, 10 - October, 11 - November, 12 - December");
        int monthAnswer;
        try{
             monthAnswer = Integer.parseInt(in.nextLine());
        }
        catch (NumberFormatException e){
            System.out.println("Incorrect input, try again");
            return getDate();
        }
        if (monthAnswer <= 12 && monthAnswer >= 1) {
            month = monthAnswer;
        }
        else{
            System.out.println("Incorrect input, try again");
            return getDate();
        }
        int monthLength =  LocalDate.of(year, month, 1 ).lengthOfMonth();
        System.out.println(MessageFormat.format("Choose the day from 1 to {0}", monthLength));
        int dayAnswer;
        try{
            dayAnswer = Integer.parseInt(in.nextLine());
        }
        catch (NumberFormatException e){
            System.out.println("Incorrect input, try again");
            return getDate();
        }
        if ( dayAnswer <= monthLength && dayAnswer >= 1){
            day = dayAnswer;
        }
        else{
            System.out.println("Incorrect input, try again");
            return getDate();
        }
        System.out.println("Choose the hours from 0 to 24");
        int hourAnswer;
        try{
            hourAnswer = Integer.parseInt(in.nextLine());
        }
        catch (NumberFormatException e){
            System.out.println("Incorrect input, try again");
            return getDate();
        }
        if ( hourAnswer <= 24 && hourAnswer >= 0){
            hour = hourAnswer;
        }
        else{
            System.out.println("Incorrect input, try again");
            return getDate();
        }
        System.out.println("Choose the minutes from 0 to 60");
        int minuteAnswer;
        try{
            minuteAnswer = Integer.parseInt(in.nextLine());
        }
        catch (NumberFormatException e){
            System.out.println("Incorrect input, try again");
            return getDate();
        }
        if ( minuteAnswer <= 60 && minuteAnswer >= 0){
            minute = minuteAnswer;
        }
        else{
            System.out.println("Incorrect input, try again");
            return getDate();
        }
        if (!isDifferentDate)
            date = LocalDateTime.of(year, month, day, hour, minute, second);
        return date;
    }

    public static String addNote() {
        Scanner in = new Scanner(System.in);
        System.out.println("Write your note");
        String noteText = in.nextLine();
        System.out.println("When will it happen?");
        LocalDateTime eventDate = getDate();
        LocalDateTime remindDate = LocalDateTime.of(1, 1, 1, 1, 1, 1);
        boolean isRemind = false;
        while (!isRemind) {
            System.out.println("Do you want a reminder? Answer 1 - yes 2 - no");
            String remindAnswer = in.nextLine();
            if (remindAnswer.equals("1")) {
                isRemind = true;
                remindDate = getDate();
            }
            else if (remindAnswer.equals("2")){
                break;
            }
            else{
                System.out.println("Incorrect input, try again");
            }
        }
        Note note = new Note(noteText, eventDate, remindDate);
        notes.add(note);
        updateCurrentDayNotes();
        if (isRemind){
            return "You have a new note {0} on {1}, remind on {2}".format(noteText, eventDate, remindDate);
        }
        return "You have a new note {0} on {1} (no remind)".format(noteText, eventDate);
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

        Iterator<Note> notesIterator = notes.iterator();
        while (notesIterator.hasNext()) {
            Note note = notesIterator.next();
            if (note.getEventDate().isBefore(today)){
                notesIterator.remove();
            }
            if (note.getRemindDate().toLocalDate().compareTo(today.toLocalDate()) == 0){
                currentDayNotes.put(note.getRemindDate(),note); //Добавляет заметку в буфер заметок с напоминанием сегодня
            }
            // TODO: fix ConcurrentModificationException ( изменяем коллекцию пока бежим по ней)
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
