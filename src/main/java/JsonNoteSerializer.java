import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class JsonNoteSerializer implements NoteSerializer {


    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void serializeNotes(SortedSet<Note> notes) {
        try{
            JsonWriter writer = new JsonWriter(new FileWriter("Notes.json"));
            writer.beginArray();
            for (Note note: notes) {
                writer.beginObject();
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
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SortedSet<Note> deserializeNotes() {
        SortedSet<Note> notes = new TreeSet<>(Comparator.comparing(Note::getRemindDate));
        String fieldName;
        try {
            JsonReader reader = new JsonReader(new FileReader("Notes.json"));
            reader.beginArray();
            String noteText = null;
            LocalDateTime eventDate = null;
            while (reader.hasNext()) {
                JsonToken nextToken = reader.peek();
                if (JsonToken.BEGIN_OBJECT.equals(nextToken)) {
                    reader.beginObject();
                }
                else if (JsonToken.NAME.equals(nextToken)) {
                    fieldName = reader.nextName();
                    if ("note".equals(fieldName)) {
                        noteText = reader.nextString();
                    }
                    else if ("eventDate".equals(fieldName)) {
                        eventDate = LocalDateTime.parse(reader.nextString(), formatter);
                    }
                    else if ("remindDate".equals(fieldName)) {
                        LocalDateTime remindDate = LocalDateTime.parse(reader.nextString(), formatter);
                        notes.add(new Note(noteText, eventDate, remindDate));
                        reader.endObject();
                    }
                }
            }
            reader.endArray();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return notes;
    }
}
