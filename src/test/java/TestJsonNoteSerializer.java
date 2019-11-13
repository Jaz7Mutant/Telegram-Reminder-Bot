import org.junit.Assert;
import org.junit.Test;
import com.jaz7.reminder.JsonNoteSerializer;
import com.jaz7.reminder.Note;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

@SuppressWarnings("SpellCheckingInspection")
public class TestJsonNoteSerializer {

    @Test
    public void testJsonSerialize(){
        SortedSet<Note> notes = new TreeSet<Note>(Comparator.comparing(Note::getRemindDate));
        Note note1 = new Note("nkfjfrje4","note1",
                LocalDateTime.of(2019,10,30,22,50),
                LocalDateTime.of(2019,9,29,0,0),
                false, 0, "egwogkerkm");
        notes.add(note1);
        JsonNoteSerializer jsonNoteSerializer = new JsonNoteSerializer();
        jsonNoteSerializer.serializeNotes(notes);
        String line;
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
                "\"remindDate\":\"2019-09-29 00:00\",\"isRepeatable\":false,\"remindPeriod\":0,\"token\":\"egwogkerkm\"}]";
        Assert.assertEquals(expectedJsonString, jsonString.toString());
    }

    @Test
    public void testJsonDeserialize(){
        SortedSet<Note> notes = new TreeSet<Note>(Comparator.comparing(Note::getRemindDate));
        Note note1 = new Note("kjfhk1jw3","note1",
                LocalDateTime.of(2019,10,30,22,50),
                LocalDateTime.of(2019,9,29,0,0),
                false, 0, "oeffer");
        Note note2 = new Note("dflj1ewr","note2",
                LocalDateTime.of(2019,11,30,22,50),
                LocalDateTime.of(2019,10,10,10,10),
                false, 0, "null");
        notes.add(note2);
        notes.add(note1);
        JsonNoteSerializer jsonNoteSerializer = new JsonNoteSerializer();
        jsonNoteSerializer.serializeNotes(notes);
        SortedSet<Note> jsonNotes = jsonNoteSerializer.deserializeNotes();
        Assert.assertEquals(notes, jsonNotes);
    }
}
