import inputOutput.ConsoleIO;
import org.junit.Assert;
import org.junit.Test;
import reminder.*;

import java.util.HashMap;

public class TestReminder {
    // тут все тесты проверяют только изменения userStates и addingStates
    @Test
    public void testAddNote(){
        ConsoleIO consoleIO = new ConsoleIO();
        Reminder reminder = new Reminder(consoleIO,  60 , new JsonNoteSerializer());
        String chatId = "sfjwo3";
        NoteKeeper noteKeeper = new NoteKeeper(chatId, consoleIO, reminder, new JsonNoteSerializer());
        reminder.userStates = new HashMap<String, NoteKeeper>();
        reminder.userStates.put(chatId, noteKeeper);
        reminder.addNote("", chatId);
        Assert.assertEquals(UserStates.ADDING, Reminder.userStates.get(chatId).currentState);
        Assert.assertEquals(AddingStates.SET_TEXT, Reminder.userStates.get(chatId).addingState);
    }

    @Test
    public void testRemoveNote(){
        ConsoleIO consoleIO = new ConsoleIO();
        Reminder reminder = new Reminder(consoleIO,  60, new JsonNoteSerializer() );
        String chatId = "sfjwo3";
        NoteKeeper noteKeeper = new NoteKeeper(chatId, consoleIO, reminder, new JsonNoteSerializer());
        reminder.userStates = new HashMap<String, NoteKeeper>();
        reminder.userStates.put(chatId, noteKeeper);
        reminder.removeNote("", chatId);
        Assert.assertEquals(UserStates.IDLE, reminder.userStates.get(chatId).currentState);
    }

    @Test
    public void testShowNotes(){
        ConsoleIO consoleIO = new ConsoleIO();
        Reminder reminder = new Reminder(consoleIO,  60, new JsonNoteSerializer() );
        String chatId = "sfjwo3";
        NoteKeeper noteKeeper = new NoteKeeper(chatId, consoleIO, reminder, new JsonNoteSerializer());
        reminder.userStates = new HashMap<String, NoteKeeper>();
        reminder.userStates.put(chatId, noteKeeper);
        reminder.showUserNotes("", chatId);
        Assert.assertEquals(UserStates.SHOWING, reminder.userStates.get(chatId).currentState);
    }

}
