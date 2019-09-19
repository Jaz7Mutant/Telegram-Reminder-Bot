import java.time.LocalDateTime;

public class Note {
    private String text;
    private LocalDateTime eventDate;
    private LocalDateTime noticeDate;

    public Note(String text, LocalDateTime eventDate, LocalDateTime noticeDate) {
        this.text = text;
        this.eventDate = eventDate;
        this.noticeDate = noticeDate;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getNoticeDate() {
        return noticeDate;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }
}
