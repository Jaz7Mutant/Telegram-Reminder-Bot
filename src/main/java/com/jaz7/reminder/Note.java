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
    private boolean isRepeatable = false;
    private long remindPeriod;

    public Note(
            String chatId,
            String text,
            LocalDateTime eventDate,
            LocalDateTime remindDate,
            long remindPeriod,
            String token) {
        this.chatId = chatId;
        this.text = text;
        this.eventDate = eventDate;
        this.remindDate = remindDate;
        if (remindPeriod != 0) {
            this.isRepeatable = true;
        }
        this.remindPeriod = remindPeriod;
        this.token = token;
    }

    public String getChatId() {
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

    public boolean isRepeatable() {
        return isRepeatable;
    }

    public long getRemindPeriod() {
        return remindPeriod;
    }

    public Note copy(String newChatId) {
        return new Note(newChatId, new String(text), eventDate.plusMinutes(0), remindDate.plusMinutes(0), remindPeriod, token);
    }

    public void deleteBeforehandRemind(SortedSet<Note> notes) {
        if (!isRepeatable) {
            remindDate = eventDate;
        } else {
            notes.remove(this);
            if (remindPeriod == 30) {
                eventDate = eventDate.plusMonths(1);
                remindDate = remindDate.plusMonths(1);
            } else {
//                eventDate = eventDate.plusMinutes(1);
//                remindDate = remindDate.plusMinutes(1);
                eventDate = eventDate.plusDays(remindPeriod); //todo Для тестов периодических напоминаний
                remindDate = remindDate.plusDays(remindPeriod);
            }
            notes.add(this);
        }
    }

    public String toStringValue() {
        return String.format("'%s', '%s', '%s', '%s', '%s', %d",
                token, chatId, text, eventDate.format(AbstractNoteSerializer.formatter), remindDate.format(AbstractNoteSerializer.formatter), remindPeriod);
    }
}
