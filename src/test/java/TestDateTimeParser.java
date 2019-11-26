import com.jaz7.bot.BotOptions;
import com.jaz7.inputOutput.ConsoleIO;
import com.jaz7.inputOutput.UserIO;
import com.jaz7.reminder.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("SpellCheckingInspection")
public class TestDateTimeParser {
    public BotOptions botOptions = new BotOptions();

    @Test
    public void testUpdateCurrentDate() {
        DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
        dateTimeParser.updateCurrentDate();
        String[] expectedYears = new String[]{
                Integer.toString(LocalDateTime.now().getYear()),
                Integer.toString(LocalDateTime.now().plusYears(1).getYear()),
                "Other"};
        Assert.assertArrayEquals(expectedYears, dateTimeParser.years);
    }

    private ByteArrayOutputStream output = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(output));
    }

//    @Test //todo fix tests
//    public void testSetWrongYear() {
//        DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
//        dateTimeParser.updateCurrentDate();
//        AddingState state = dateTimeParser.setYear(new GregorianCalendar(), "",
//                "chatId", AddingState.SET_YEAR);
//        AddingState state1 = dateTimeParser.setYear(new GregorianCalendar(), "100",
//                "chatId", AddingState.SET_YEAR);
//        AddingState state2 = dateTimeParser.setYear(new GregorianCalendar(), "2036",
//                "chatId", AddingState.SET_YEAR);
//        Assert.assertEquals("Wrong format\r\nIllegal year\r\nIllegal year\r\n", output.toString());
//        AddingState[] states = new AddingState[3];
//        Arrays.fill(states, AddingState.SET_YEAR);
//        Assert.assertArrayEquals(states, new AddingState[]{state, state1, state2});
//    }

//    @Test //todo fix
//    public void testSetYear() {
//        DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
//        dateTimeParser.updateCurrentDate();
//        AddingState state = dateTimeParser.setYear(new GregorianCalendar(), Integer.toString(dateTimeParser.years.length - 1),
//                "chatId", AddingState.SET_YEAR);
//        Assert.assertEquals("Set year (yyyy)\r\n", output.toString());
//        AddingState state1 = dateTimeParser.setYear(new GregorianCalendar(), "1",
//                "chatId", AddingState.SET_YEAR);
//        AddingState state2 = dateTimeParser.setYear(new GregorianCalendar(), "1",
//                "chatId", AddingState.SET_REMIND_YEAR);
//        Assert.assertEquals(AddingState.SET_MONTH, state1);
//        Assert.assertEquals(AddingState.SET_REMIND_MONTH, state2);
//    }

//    @Test //todo fix
//    public void testSetWrongMonth() {
//        DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
//        dateTimeParser.updateCurrentDate();
//        AddingState state = dateTimeParser.setMonth(new GregorianCalendar(), "",
//                "chatId", AddingState.SET_MONTH);
//        AddingState state1 = dateTimeParser.setMonth(new GregorianCalendar(), "-1",
//                "chatId", AddingState.SET_MONTH);
//        AddingState state2 = dateTimeParser.setMonth(new GregorianCalendar(), "13",
//                "chatId", AddingState.SET_MONTH);
//        Assert.assertEquals("Wrong format\r\nWrong format\r\nWrong format\r\n", output.toString());
//        AddingState[] states = new AddingState[3];
//        Arrays.fill(states, AddingState.SET_MONTH);
//        Assert.assertArrayEquals(states, new AddingState[]{state, state1, state2});
//    }

//    @Test //todo fix
//    public void testSetMonth() {
//        DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
//        dateTimeParser.updateCurrentDate();
//        Calendar calendar = new GregorianCalendar();
//        String month = "1";
//        AddingState state = dateTimeParser.setMonth(calendar, month,
//                "chatId", AddingState.SET_MONTH);
//        calendar.set(Calendar.MONTH, LocalDateTime.now().plusMonths(Integer.parseInt(month) - 1).getMonthValue());
//        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//        String[] days = new String[daysInMonth];
//        for (int i = 1; i <= daysInMonth; i++) {
//            days[i - 1] = Integer.toString(i);
//        }
//        StringBuilder respond = new StringBuilder();
//        respond.append("Choose day\r\n");
//        for (int i = 0; i < days.length; i++) {
//            respond.append(i + 1).append(". ").append(days[i]).append("\r\n");
//        }
//        Assert.assertEquals(respond.toString(), output.toString());
//        AddingState state1 = dateTimeParser.setMonth(calendar, month,
//                "chatId", AddingState.SET_REMIND_MONTH);
//        Assert.assertEquals(AddingState.SET_DAY, state);
//        Assert.assertEquals(AddingState.SET_REMIND_DAY, state1);
//    }

//    @Test
//    public void testSetWrongDay() {
//        DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
//        dateTimeParser.updateCurrentDate();
//        Calendar calendar = new GregorianCalendar();
//        String month = "1";
//        dateTimeParser.setMonth(calendar, month, "chatId", AddingState.SET_MONTH);
//        output = new ByteArrayOutputStream();
//        setUpStreams();
//        dateTimeParser.setDay(calendar, "32", "chatId", AddingState.SET_DAY);
//        dateTimeParser.setDay(calendar, "-1", "chatId", AddingState.SET_DAY);
//        Assert.assertEquals("Wrong format\r\nWrong format\r\n", output.toString());
//    }

//    @Test
//    public void testSetDay() {
//        DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
//        dateTimeParser.updateCurrentDate();
//        Calendar calendar = new GregorianCalendar();
//        String month = "1";
//        dateTimeParser.setMonth(calendar, month, "chatId", AddingState.SET_MONTH);
//        output = new ByteArrayOutputStream();
//        setUpStreams();
//        AddingState state = dateTimeParser.setDay(calendar, "1", "chatId", AddingState.SET_DAY);
//        Assert.assertEquals("Set time (hh:mm)\r\n", output.toString());
//        Assert.assertEquals(AddingState.SET_TIME, state);
//        AddingState state1 = dateTimeParser.setDay(calendar, "1", "chatId", AddingState.SET_REMIND_DAY);
//        Assert.assertEquals(AddingState.SET_REMIND_TIME, state1);
//    }

    @Test
    public void testSetWrongTime(){
        UserIO userIO = new ConsoleIO();
        NoteSerializer noteSerializer = new JsonNoteSerializer();
        DateTimeParser.updateCurrentDate();
        NoteKeeper noteKeeper = new NoteKeeper("chatId", userIO, new Reminder(userIO, 10, noteSerializer), noteSerializer);
        noteKeeper.currentState = UserState.ADDING;
        noteKeeper.noteAdder.addingState = AddingState.SET_TIME;
        noteKeeper.doNextStep("12 12");
        AddingState state = noteKeeper.noteAdder.addingState;
        noteKeeper.noteAdder.addingState = AddingState.SET_TIME;
        noteKeeper.doNextStep("dfsjm");
        AddingState state1 = noteKeeper.noteAdder.addingState;
        noteKeeper.noteAdder.addingState = AddingState.SET_TIME;
        noteKeeper.doNextStep("");
        AddingState state2 = noteKeeper.noteAdder.addingState;
        Assert.assertEquals("Wrong format\r\nWrong format\r\nWrong format\r\n", output.toString());
        Assert.assertEquals(AddingState.SET_TIME, state);
        Assert.assertEquals(AddingState.SET_TIME, state1);
        Assert.assertEquals(AddingState.SET_TIME, state2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");
        noteKeeper.doNextStep(LocalDateTime.now().minusHours(5).format(formatter));
        AddingState state3 = noteKeeper.noteAdder.addingState;
        noteKeeper.noteAdder.addingState = AddingState.SET_REMIND_TIME;
        noteKeeper.doNextStep(LocalDateTime.now().minusHours(5).format(formatter));
        AddingState state4 = noteKeeper.noteAdder.addingState;
        Assert.assertEquals(AddingState.SET_MONTH, state3);
        Assert.assertEquals(AddingState.SET_REMIND_MONTH, state4);
        Assert.assertTrue(output.toString().contains("Wrong date\r\n"));
    }

    @Test
    public void testSetDate(){
        UserIO userIO = new ConsoleIO();
        NoteSerializer noteSerializer = new JsonNoteSerializer();
        NoteKeeper noteKeeper = new NoteKeeper("ss", userIO, new Reminder(userIO, 10, noteSerializer), noteSerializer);
        DateTimeParser.updateCurrentDate();
        noteKeeper.currentState = UserState.ADDING;
        noteKeeper.noteAdder.addingState = AddingState.SET_TEXT;
        noteKeeper.doNextStep("HUITESTTEXTNOTE");
        Assert.assertEquals(AddingState.SET_YEAR, noteKeeper.noteAdder.addingState);
        noteKeeper.doNextStep("2020");
        Assert.assertEquals(AddingState.SET_MONTH, noteKeeper.noteAdder.addingState);
        noteKeeper.doNextStep("10");
        Assert.assertEquals(AddingState.SET_DAY, noteKeeper.noteAdder.addingState);
        noteKeeper.doNextStep("10");
        Assert.assertEquals(AddingState.SET_TIME, noteKeeper.noteAdder.addingState);
        noteKeeper.doNextStep(ZonedDateTime.now().plusHours(5).format(DateTimeFormatter.ofPattern("hh:mm")));
        AddingState state = noteKeeper.noteAdder.addingState;
        Assert.assertEquals(AddingState.SET_REMIND, state);
        noteKeeper.doNextStep("4");
        noteKeeper.currentState = UserState.ADDING;
        noteKeeper.noteAdder.addingState = AddingState.SET_REMIND_YEAR;
        noteKeeper.doNextStep("2020");
        noteKeeper.doNextStep("10");
        noteKeeper.doNextStep("10");
        noteKeeper.noteAdder.addingState = AddingState.SET_REMIND_TIME;
        noteKeeper.doNextStep(ZonedDateTime.now().plusHours(5).format(DateTimeFormatter.ofPattern("hh:mm")));
        AddingState state1 = noteKeeper.noteAdder.addingState;
        Assert.assertEquals(AddingState.IDLE, state1);
    }
}