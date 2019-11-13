import com.jaz7.bot.BotOptions;
import com.jaz7.inputOutput.ConsoleIO;
import com.jaz7.reminder.*;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class TestMeeting {
    BotOptions botOptions = new BotOptions();
    @Test
    public void testJoinMeeting(){
        Note meet = new Note("1223","test",
                LocalDateTime.of(2019, 12, 13, 23,22),
                LocalDateTime.of(2019, 12, 13, 23,22),
                false, 0,"token");
        ConsoleIO consoleIO = new ConsoleIO();
        JsonNoteSerializer noteSerializer = new JsonNoteSerializer();
        SortedSet<Note> notes = new TreeSet<>(Comparator.comparing(Note::getRemindDate).
                thenComparing(Note::getText).
                thenComparing(Note::getChatId).
                thenComparing(Note::hashCode));
        notes.add(meet);
        noteSerializer.serializeNotes(notes);
        Reminder reminder = new Reminder(consoleIO, 10, noteSerializer );
        NoteKeeper noteKeeper = new NoteKeeper("1333", consoleIO, reminder, noteSerializer);
        noteKeeper.currentState = UserState.JOINING;
        noteKeeper.doNextStep("token");
        boolean isJoin = false;
        for (Note note: reminder.notes)
            if (note.getText().equals(meet.getText()) && note.getEventDate().equals(meet.getEventDate()) &&
                    note.getRemindDate().equals(meet.getRemindDate()) && note.isRepeatable() == meet.isRepeatable() &&
                    note.getRemindPeriod() == note.getRemindPeriod() && !note.getChatId().equals(meet.getChatId())) {
                isJoin = true;
            }
        Assert.assertTrue(isJoin);
    }

    @Test
    public void testNotesWithTheSameTime() {
        Note note1 = new Note("1223", "first",
                LocalDateTime.of(2019, 12, 13, 23, 22),
                LocalDateTime.of(2019, 12, 13, 23, 22),
                false, 0, "dkfl");
        Note note2 = new Note("1223", "second",
                LocalDateTime.of(2019, 12, 13, 23, 22),
                LocalDateTime.of(2019, 12, 13, 23, 22),
                false, 0, "dkfl");
        ConsoleIO consoleIO = new ConsoleIO();
        JsonNoteSerializer noteSerializer = new JsonNoteSerializer();
        SortedSet<Note> notes = new TreeSet<>(Comparator.comparing(Note::getRemindDate).
                thenComparing(Note::getText).
                thenComparing(Note::getChatId).
                thenComparing(Note::hashCode));
        notes.add(note1);
        notes.add(note2);
        noteSerializer.serializeNotes(notes);
        Reminder reminder = new Reminder(consoleIO, 10, noteSerializer);
        Assert.assertEquals(2, reminder.notes.size());
        Note actual1 = reminder.notes.first();
        Note actual2 = reminder.notes.last();
        Assert.assertTrue(note1.getText().equals(actual1.getText()) && note1.getEventDate().equals(actual1.getEventDate()) &&
                note1.getRemindDate().equals(actual1.getRemindDate()) && note1.isRepeatable() == actual1.isRepeatable() &&
                note1.getRemindPeriod() == actual1.getRemindPeriod() && note1.getChatId().equals(actual1.getChatId()) &&
                note1.getToken().equals(actual1.getToken()));
        Assert.assertTrue(note2.getText().equals(actual2.getText()) && note2.getEventDate().equals(actual2.getEventDate()) &&
                note2.getRemindDate().equals(actual2.getRemindDate()) && note2.isRepeatable() == actual2.isRepeatable() &&
                note2.getRemindPeriod() == actual2.getRemindPeriod() && note2.getChatId().equals(actual2.getChatId()) &&
                note2.getToken().equals(actual2.getToken()));
    }
}
