import inputOutput.ConsoleIO;
import org.junit.Assert;
import org.junit.Test;
import reminder.AddingStates;
import reminder.NoteKeeper;
import reminder.Reminder;
import reminder.UserStates;

import java.util.HashMap;

public class TestReminder {

    @Test
    public void TestAddNote(){
        ConsoleIO consoleIO = new ConsoleIO();
        Reminder reminder = new Reminder(consoleIO,  60 );
        String chatId = "sfjwo3";
        NoteKeeper noteKeeper = new NoteKeeper(chatId, consoleIO, reminder);
        reminder.userStates = new HashMap<String, NoteKeeper>();
        reminder.userStates.put(chatId, noteKeeper);
        reminder.addNote("", chatId);
        Assert.assertEquals(UserStates.ADDING, Reminder.userStates.get(chatId).currentState);
        Assert.assertEquals(AddingStates.SET_TEXT, Reminder.userStates.get(chatId).addingState);
    }

    public void TestRemoveNote(){
        ConsoleIO consoleIO = new ConsoleIO();
        Reminder reminder = new Reminder(consoleIO,  60 );
        String chatId = "sfjwo3";
        NoteKeeper noteKeeper = new NoteKeeper(chatId, consoleIO, reminder);
        reminder.userStates = new HashMap<String, NoteKeeper>();
        reminder.userStates.put(chatId, noteKeeper);
        reminder.addNote("/note", chatId);
    }

}
