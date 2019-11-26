package com.jaz7.reminder;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.SortedSet;

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
        token = "MEET" + (chatId + remindDate.toString() + text.hashCode() + new Random().ints()).hashCode(); // todo uuid
    }

    public boolean isRepeatable(){
        return isRepeatable;
    }

    public long getRemindPeriod(){
        return remindPeriod;
    }

    public Note copy(String newChatId){
        return new Note(newChatId, new String(text), eventDate.plusMinutes(0), remindDate.plusMinutes(0), isRepeatable, remindPeriod, token);
    }

    public void deleteBeforehandRemind(SortedSet<Note> notes){
        if (!isRepeatable) {
            remindDate = eventDate;
        }
        else {
            notes.remove(this);
            if (remindPeriod == 30){
                eventDate = eventDate.plusMonths(1);
            }
            else {
         //       eventDate = eventDate.plusMinutes(5);
                eventDate = eventDate.plusDays(remindPeriod); //todo
            }
            remindDate = eventDate;
            notes.add(this);
        }
    }
}
