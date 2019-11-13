package com.jaz7.reminder;

import java.time.LocalDateTime;
import java.util.Random;

public class Note {
    private String chatId;
    private String text;
    private LocalDateTime eventDate;
    private LocalDateTime remindDate;
    private String token;

    public Note(
            String chatId,
            String text,
            LocalDateTime eventDate,
            LocalDateTime remindDate) {
        this.chatId = chatId;
        this.text = text;
        this.eventDate = eventDate;
        this.remindDate = remindDate;
        this.token = null;
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

    public String getToken() {
        return token;
    }

    public void setToken() {
        token = "MEET" + (chatId + remindDate.toString() + text.hashCode() + new Random().ints()).hashCode();
    }

    public Note copy(String newChatId){
        return new Note(newChatId, text, eventDate, remindDate);
    }

    public void deleteBeforehandRemind(){
        remindDate = eventDate;
    }
}
