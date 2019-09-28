import java.time.LocalDateTime;

public class Note {
    private String text;
    private LocalDateTime eventDate;
    private LocalDateTime remindDate;

    public Note(String text, LocalDateTime eventDate, LocalDateTime remindDate) {
        this.text = text;
        this.eventDate = eventDate;
        this.remindDate = remindDate;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public LocalDateTime getRemindDate() {
        return remindDate;
    }
}
