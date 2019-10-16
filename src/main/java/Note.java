import java.time.LocalDateTime;

public class Note {
    private long chatId;
    private String text;
    private LocalDateTime eventDate;
    private LocalDateTime remindDate;

    public Note(
            long userId,
            String text, LocalDateTime eventDate, LocalDateTime remindDate) {
        this.chatId = userId;
        this.text = text;
        this.eventDate = eventDate;
        this.remindDate = remindDate;
    }

    public long getChatId(){
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
