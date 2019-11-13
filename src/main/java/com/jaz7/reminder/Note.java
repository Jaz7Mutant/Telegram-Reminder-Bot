package com.jaz7.reminder;

import java.time.LocalDateTime;
import java.util.Random;

public class Note {
    private String chatId;
    private String text;
    private LocalDateTime eventDate;
    private LocalDateTime remindDate;
    private String token;
    private boolean isRepeatable;
    private long remindPeriod;


    public Note(
            String chatId,
            String text,
            LocalDateTime eventDate,
            LocalDateTime remindDate,
            boolean isRepeatable,
            long remindPeriod) {
        this.chatId = chatId;
        this.text = text;
        this.eventDate = eventDate;
        this.remindDate = remindDate;
        this.isRepeatable = isRepeatable;
        this.remindPeriod = remindPeriod;
        this.token = null;
    }

    public Note(
            String chatId,
            String text,
            LocalDateTime eventDate,
            LocalDateTime remindDate,
            boolean isRepeatable,
            long remindPeriod,
            String token) {
        this.chatId = chatId;
        this.text = text;
        this.eventDate = eventDate;
        this.remindDate = remindDate;
        this.isRepeatable = isRepeatable;
        this.remindPeriod = remindPeriod;
        this.token = token;
    }

    public boolean isRepeatable(){
        return isRepeatable;
    }

    public long getRemindPeriod(){
        return remindPeriod;
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
        return new Note(newChatId, text, eventDate, remindDate, isRepeatable, remindPeriod);
    }

    public void deleteBeforehandRemind(){
        remindDate = eventDate;
    }
}
