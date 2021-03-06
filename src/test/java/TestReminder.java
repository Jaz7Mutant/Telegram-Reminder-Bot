//import com.jaz7.bot.BotOptions;
//import com.jaz7.inputOutput.ConsoleIO;
//import org.junit.Assert;
//import org.junit.Test;
//import com.jaz7.reminder.*;
//
//import java.util.HashMap;
//
//@SuppressWarnings("SpellCheckingInspection")
//public class TestReminder {
//    // тут все тесты проверяют только изменения userStates и addingStates
//    public BotOptions botOptions = new BotOptions();
//    @Test
//    public void testAddNote(){
//        ConsoleIO consoleIO = new ConsoleIO();
//        Reminder reminder = new Reminder(consoleIO,  60 , new JsonNoteSerializer());
//        String chatId = "sfjwo3";
//        NoteKeeper noteKeeper = new NoteKeeper(chatId, consoleIO, reminder, new JsonNoteSerializer());
//        reminder.userStates = new HashMap<String, NoteKeeper>();
//        reminder.userStates.put(chatId, noteKeeper);
//        reminder.addNote("", chatId);
//        Assert.assertEquals(UserState.ADDING, Reminder.userStates.get(chatId).currentState);
//        Assert.assertEquals(AddingState.SET_TEXT, Reminder.userStates.get(chatId).noteAdder.addingState);
//    }
//
//    @Test
//    public void testRemoveNote(){
//        ConsoleIO consoleIO = new ConsoleIO();
//        Reminder reminder = new Reminder(consoleIO,  60, new JsonNoteSerializer() );
//        String chatId = "sfjwo3";
//        NoteKeeper noteKeeper = new NoteKeeper(chatId, consoleIO, reminder, new JsonNoteSerializer());
//        reminder.userStates = new HashMap<String, NoteKeeper>();
//        reminder.userStates.put(chatId, noteKeeper);
//        reminder.removeNote("", chatId);
//        Assert.assertEquals(UserState.IDLE, reminder.userStates.get(chatId).currentState);
//    }
//
//    @Test
//    public void testShowNotes(){
//        ConsoleIO consoleIO = new ConsoleIO();
//        Reminder reminder = new Reminder(consoleIO,  60, new JsonNoteSerializer() );
//        String chatId = "sfjwo3";
//        NoteKeeper noteKeeper = new NoteKeeper(chatId, consoleIO, reminder, new JsonNoteSerializer());
//        reminder.userStates = new HashMap<String, NoteKeeper>();
//        reminder.userStates.put(chatId, noteKeeper);
//        reminder.showUserNotes("", chatId);
//        Assert.assertEquals(UserState.SHOWING, reminder.userStates.get(chatId).currentState);
//    }
//
//    @Test
//    public void testAddMeeting(){
//        ConsoleIO consoleIO = new ConsoleIO();
//        Reminder reminder = new Reminder(consoleIO,  60, new JsonNoteSerializer() );
//        String chatId = "sfjwo3";
//        NoteKeeper noteKeeper = new NoteKeeper(chatId, consoleIO, reminder, new JsonNoteSerializer());
//        reminder.userStates = new HashMap<String, NoteKeeper>();
//        reminder.userStates.put(chatId, noteKeeper);
//        reminder.addMeeting("",chatId);
//        Assert.assertEquals(UserState.ADDING, reminder.userStates.get(chatId).currentState);
//        Assert.assertEquals(AddingState.SET_MEETING, reminder.userStates.get(chatId).noteAdder.addingState);
//    }
//
//    @Test
//    public void testJoinMeeting(){
//        ConsoleIO consoleIO = new ConsoleIO();
//        Reminder reminder = new Reminder(consoleIO,  60, new JsonNoteSerializer() );
//        String chatId = "sfjwo3";
//        NoteKeeper noteKeeper = new NoteKeeper(chatId, consoleIO, reminder, new JsonNoteSerializer());
//        reminder.userStates = new HashMap<String, NoteKeeper>();
//        reminder.userStates.put(chatId, noteKeeper);
//        reminder.joinMeeting("",chatId);
//        Assert.assertEquals(UserState.JOINING, reminder.userStates.get(chatId).currentState);
//    }
//
//}
