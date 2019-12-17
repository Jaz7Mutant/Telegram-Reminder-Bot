package com.jaz7.reminder;

import com.jaz7.serializer.AbstractNoteSerializer;

import java.time.LocalDateTime;
import java.util.SortedSet;
import java.util.UUID;
import java.util.logging.Logger;

public class Note {
    private String chatId;
    private String text;
    private LocalDateTime eventDate;
    private LocalDateTime remindDate;
    private String token;
    private boolean isRepeatable = false;
    private long remindPeriod;
    private static final Logger LOGGER = Logger.getLogger(Note.class.getSimpleName());

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
        token = UUID.randomUUID().toString().replace("-", "");
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
        LOGGER.info(chatId + ": Shifting remind...");
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
        return String.format(
                "'%s', '%s', '%s', '%s', '%s', %d",
                token,
                chatId,
                text,
                eventDate.format(AbstractNoteSerializer.formatter),
                remindDate.format(AbstractNoteSerializer.formatter),
                remindPeriod);
    }

    @Override
    public int hashCode() {
        return (chatId.hashCode() + text.hashCode() + eventDate.hashCode()) ^ remindDate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Note))
            return false;
        Note otherNote = (Note)obj;
        return chatId.equals(otherNote.chatId) && text.equals(otherNote.text) &&
            eventDate.equals(otherNote.eventDate) && remindDate.equals(otherNote.remindDate);

    }
}
