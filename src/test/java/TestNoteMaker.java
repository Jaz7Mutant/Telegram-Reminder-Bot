import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class TestNoteMaker {

    @Test
    public void TestAddNote(){
        ConsoleIO consoleIO = new ConsoleIO();
        NoteMaker noteMaker = new NoteMaker(consoleIO,  60 );
        String chatId = "sfjwo3";
        StateHolder stateHolder = new StateHolder(chatId, consoleIO, noteMaker);
        noteMaker.userStates = new HashMap<String, StateHolder>();
        noteMaker.userStates.put(chatId, stateHolder);
        noteMaker.addNote("", chatId);
        Assert.assertEquals(UserStates.ADDING, NoteMaker.userStates.get(chatId).currentState);
        Assert.assertEquals(AddingStates.SET_TEXT, NoteMaker.userStates.get(chatId).addingState);
    }

    public void TestRemoveNote(){
        ConsoleIO consoleIO = new ConsoleIO();
        NoteMaker noteMaker = new NoteMaker(consoleIO,  60 );
        String chatId = "sfjwo3";
        StateHolder stateHolder = new StateHolder(chatId, consoleIO, noteMaker);
        noteMaker.userStates = new HashMap<String, StateHolder>();
        noteMaker.userStates.put(chatId, stateHolder);
        noteMaker.addNote("/note", chatId);
    }

}
