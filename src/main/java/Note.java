import java.time.LocalDateTime;

public class Note {
    private String userId;
    private String text;
    private LocalDateTime eventDate;
    private LocalDateTime remindDate;

    public Note(String userId, String text, LocalDateTime eventDate, LocalDateTime remindDate) {
        this.userId = userId;
        this.text = text;
        this.eventDate = eventDate;
        this.remindDate = remindDate;
    }

    public String getUserId(){
        return userId;
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
