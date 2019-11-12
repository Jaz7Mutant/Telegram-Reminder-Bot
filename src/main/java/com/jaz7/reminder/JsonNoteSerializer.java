package com.jaz7.reminder;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonNoteSerializer implements NoteSerializer {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Logger LOGGER = Logger.getLogger(JsonNoteSerializer.class.getSimpleName());

    @Override
    public void serializeNotes(SortedSet<Note> notes) {
        try{
            LOGGER.info("Serializing notes...");
            JsonWriter writer = new JsonWriter(new FileWriter("Notes.json"));
            writer.beginArray();
            for (Note note: notes) {
                writer.beginObject();
                writer.name("chatId");
                writer.value(note.getChatId());
                writer.name("note");
                writer.value(note.getText());
                writer.name("eventDate");
                writer.value(note.getEventDate().format(formatter));
                writer.name("remindDate");
                writer.value(note.getRemindDate().format(formatter));
                writer.endObject();
            }
            writer.endArray();
            writer.close();
            LOGGER.info("Notes has been serialized");
        }
        catch (IOException e) {
            LOGGER.log(Level.WARNING, "Notes serializing error:", e);
        }
    }

    @Override
    public SortedSet<Note> deserializeNotes() {
        LOGGER.info("Deserializing notes...");
        SortedSet<Note> notes = new TreeSet<>(Comparator.comparing(Note::getRemindDate));
        String fieldName;
        try {
            JsonReader reader = new JsonReader(new FileReader("Notes.json"));
            reader.beginArray();
            String noteText = null;
            String chatId = null;
            LocalDateTime eventDate = null;
            while (reader.hasNext()) {
                JsonToken nextToken = reader.peek();
                if (JsonToken.BEGIN_OBJECT.equals(nextToken)) {
                    reader.beginObject();
                }
                else if (JsonToken.NAME.equals(nextToken)) {
                    fieldName = reader.nextName();
                    if ("chatId".equals(fieldName)) {
                        chatId = reader.nextString();
                    }
                    if ("note".equals(fieldName)) {
                        noteText = reader.nextString();
                    }
                    else if ("eventDate".equals(fieldName)) {
                        eventDate = LocalDateTime.parse(reader.nextString(), formatter);
                    }
                    else if ("remindDate".equals(fieldName)) {
                        LocalDateTime remindDate = LocalDateTime.parse(reader.nextString(), formatter);
                        notes.add(new Note(chatId, noteText, eventDate, remindDate));
                        reader.endObject();
                    }
                }
            }
            reader.endArray();
        }
        catch (IOException e) {
            LOGGER.log(Level.WARNING, "Notes deserializing error:", e);
        }
        LOGGER.info("Notes has been deserialized");
        return notes;
    }
}
