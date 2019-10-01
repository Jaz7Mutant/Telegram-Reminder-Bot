import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TestBot {

    //public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Test
    public void testJsonSerialize(){
        SortedSet<Note> notes = new TreeSet<Note>(Comparator.comparing(Note::getRemindDate));
        Note note1 = new Note("note1",LocalDateTime.of(2019,10,30,22,50),
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
        String expectedJsonString = "[{\"note\":\"note1\",\"eventDate\":\"2019-10-30 22:50\"," +
                "\"remindDate\":\"2019-09-29 00:00\"}]";
        Assert.assertEquals(expectedJsonString, jsonString.toString());
    }

    @Test
    public void testJsonDeserialize(){
        SortedSet<Note> notes = new TreeSet<Note>(Comparator.comparing(Note::getRemindDate));
        Note note1 = new Note("note1",LocalDateTime.of(2019,10,30,22,50),
                LocalDateTime.of(2019,9,29,0,0));
        Note note2 = new Note("note2",LocalDateTime.of(2019,11,30,22,50),
                LocalDateTime.of(2019,10,10,10,10));
        notes.add(note2);
        notes.add(note1);
        JsonNoteSerializer jsonNoteSerializer = new JsonNoteSerializer();
        jsonNoteSerializer.serializeNotes(notes);
        SortedSet<Note> jsonNotes = jsonNoteSerializer.deserializeNotes();
        //for (Note note: jsonNotes) {
        //    System.out.println(note.getText() + " " + note.getEventDate().format( formatter) + " " + note.getRemindDate().format( formatter));
        //}
        //for (Note note: notes) {
        //    System.out.println(note.getText() + " " + note.getEventDate().format( formatter) + " " + note.getRemindDate().format( formatter));
        //}
        Assert.assertEquals(notes, jsonNotes);
    }
}


