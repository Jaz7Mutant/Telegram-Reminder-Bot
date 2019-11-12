package com.jaz7.reminder;

import java.time.LocalDateTime;

public class Note {
    private String chatId;
    private String text;
    private LocalDateTime eventDate;
    private LocalDateTime remindDate;

    public Note(
            String chatId,
            String text,
            LocalDateTime eventDate,
            LocalDateTime remindDate) {
        this.chatId = chatId;
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
