import java.time.LocalDateTime;

public class Note {
    private String chatId;
    private String text;
    private LocalDateTime eventDate;
    private LocalDateTime remindDate;

    public Note(
            String userId,
            String text,
            LocalDateTime eventDate,
            LocalDateTime remindDate) {
        this.chatId = userId;
        this.text = text;
        this.eventDate = eventDate;
        this.remindDate = remindDate;
    }

    public String getChatId(){
        return chatId;
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

    public void deleteBeforehandRemind(){
        remindDate = eventDate;
    }
}
