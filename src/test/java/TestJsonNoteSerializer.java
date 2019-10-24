import org.junit.Assert;
import org.junit.Test;
import reminder.JsonNoteSerializer;
import reminder.Note;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class TestJsonNoteSerializer {

    @Test
    public void testJsonSerialize(){
        SortedSet<Note> notes = new TreeSet<Note>(Comparator.comparing(Note::getRemindDate));
        Note note1 = new Note("nkfjfrje4","note1",
                LocalDateTime.of(2019,10,30,22,50), //todo chatid
                LocalDateTime.of(2019,9,29,0,0));
        notes.add(note1);
        JsonNoteSerializer jsonNoteSerializer = new JsonNoteSerializer();
        jsonNoteSerializer.serializeNotes(notes);
        String line = null;
        StringBuilder jsonString = new StringBuilder();
        try{
            BufferedReader in = new BufferedReader(new FileReader("Notes.json"));
            while ((line = in.readLine()) != null) {
                jsonString.append(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String expectedJsonString = "[{\"chatId\":\"nkfjfrje4\",\"note\":\"note1\",\"eventDate\":\"2019-10-30 22:50\"," +
                "\"remindDate\":\"2019-09-29 00:00\"}]";
        Assert.assertEquals(expectedJsonString, jsonString.toString());
    }

    @Test
    public void testJsonDeserialize(){
        SortedSet<Note> notes = new TreeSet<Note>(Comparator.comparing(Note::getRemindDate));
        Note note1 = new Note("kjfhk1jw3","note1",
                LocalDateTime.of(2019,10,30,22,50), //todo chatid
                LocalDateTime.of(2019,9,29,0,0));
        Note note2 = new Note("dflj1ewr","note2",
                LocalDateTime.of(2019,11,30,22,50), // todo chatid
                LocalDateTime.of(2019,10,10,10,10));
        notes.add(note2);
        notes.add(note1);
        JsonNoteSerializer jsonNoteSerializer = new JsonNoteSerializer();
        jsonNoteSerializer.serializeNotes(notes);
        SortedSet<Note> jsonNotes = jsonNoteSerializer.deserializeNotes();
        Assert.assertEquals(notes, jsonNotes);
    }
}
