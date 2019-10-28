import org.junit.Assert;
import org.junit.Test;
import reminder.Note;

import java.time.LocalDateTime;

public class TestNote {
    @Test
    public void testNote(){
        Note note = new Note("wlfn", "note",
                LocalDateTime.of(2019,10,30,22,5),
                LocalDateTime.of(2019,9,29,0,0));
        Assert.assertEquals("wlfn", note.getChatId());
        Assert.assertEquals("note", note.getText());
        Assert.assertEquals(LocalDateTime.of(2019,10,30,22,5),
                note.getEventDate());
        Assert.assertEquals(LocalDateTime.of(2019,9,29,0,0),
                note.getRemindDate());
        note.deleteBeforehandRemind();
        Assert.assertEquals(note.getEventDate(), note.getRemindDate());
    }

}
