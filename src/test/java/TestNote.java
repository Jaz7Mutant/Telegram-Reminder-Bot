import com.jaz7.reminder.Note;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

@SuppressWarnings("SpellCheckingInspection")
public class TestNote {
    @Test
    public void testNote(){
        Note note = new Note("wlfn", "note",
                LocalDateTime.of(2019,10,30,22,5),
                LocalDateTime.of(2019,9,29,0,0),
                 0, "dfmv");
        Assert.assertEquals("wlfn", note.getChatId());
        Assert.assertEquals("note", note.getText());
        Assert.assertEquals(LocalDateTime.of(2019,10,30,22,5),
                note.getEventDate());
        Assert.assertEquals(LocalDateTime.of(2019,9,29,0,0),
                note.getRemindDate());
        SortedSet<Note> notes = new TreeSet<>(Comparator.comparing(Note::getRemindDate));
        notes.add(note);
        note.deleteBeforehandRemind(notes);
        Assert.assertEquals(note.getEventDate(), note.getRemindDate());
    }

}