import inputOutput.ConsoleIO;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import reminder.AddingStates;
import reminder.DateTimeParser;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.YearMonth;
import java.util.Arrays;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TestDateTimeParser{
    @Test
    public void TestUpdateCurrentDate(){
     DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
     dateTimeParser.updateCurrentDate();
     String[] expectedYears = new String[]{
             Integer.toString(LocalDateTime.now().getYear()),
             Integer.toString(LocalDateTime.now().plusYears(1).getYear()),
             "Other"};
     Assert.assertArrayEquals(expectedYears, dateTimeParser.years);
    }

    private ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOut = System.out;

    @Before
    public void setUpStreams(){
        System.setOut(new PrintStream(output));
    }

    @After
    public void restoreStreams(){
        System.setOut(originalOut);
    }

    @Test
    public void TestSetWrongYear(){
        DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
        dateTimeParser.updateCurrentDate();
        AddingStates state = dateTimeParser.setYear(new GregorianCalendar(), "",
                "chatId", AddingStates.SET_YEAR);
        AddingStates state1 = dateTimeParser.setYear(new GregorianCalendar(), "100",
                "chatId", AddingStates.SET_YEAR);
        AddingStates state2 = dateTimeParser.setYear(new GregorianCalendar(), "2036",
                "chatId", AddingStates.SET_YEAR);
        Assert.assertEquals("Wrong format\r\nIllegal year\r\nIllegal year\r\n", output.toString());
        AddingStates[] states = new AddingStates[3];
        Arrays.fill(states, AddingStates.SET_YEAR);
        Assert.assertArrayEquals(states, new AddingStates[]{state, state1,state2});
    }

    @Test
    public void TestSetYear(){
        DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
        dateTimeParser.updateCurrentDate();
        AddingStates state = dateTimeParser.setYear(new GregorianCalendar(), Integer.toString(dateTimeParser.years.length - 1),
                "chatId", AddingStates.SET_YEAR);
        Assert.assertEquals("Set year (yyyy)\r\n", output.toString());
        AddingStates state1 = dateTimeParser.setYear(new GregorianCalendar(), "1",
                "chatId", AddingStates.SET_YEAR);
        Assert.assertEquals(AddingStates.SET_MONTH, state1);
        AddingStates state2 = dateTimeParser.setYear(new GregorianCalendar(), "1",
                "chatId", AddingStates.SET_MONTH);
        Assert.assertEquals(AddingStates.SET_REMIND_MONTH, state2);
    }

    @Test
    public void TestSetWrongMonth(){
        DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
        dateTimeParser.updateCurrentDate();
        AddingStates state = dateTimeParser.setMonth(new GregorianCalendar(), "",
                "chatId", AddingStates.SET_MONTH);
        AddingStates state1 = dateTimeParser.setMonth(new GregorianCalendar(), "-1",
                "chatId", AddingStates.SET_MONTH);
        AddingStates state2 = dateTimeParser.setMonth(new GregorianCalendar(), "12",
                "chatId", AddingStates.SET_MONTH);
        Assert.assertEquals("Wrong format\r\nWrong format\r\nWrong format\r\n", output.toString());
        AddingStates[] states = new AddingStates[3];
        Arrays.fill(states, AddingStates.SET_MONTH);
        Assert.assertArrayEquals(states, new AddingStates[]{state, state1,state2});
    }

    @Test
    public void TestSetMonth(){
        DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
        dateTimeParser.updateCurrentDate();
        Calendar calendar = new GregorianCalendar();
        String month = "1";
        AddingStates state = dateTimeParser.setMonth(calendar, month,
                "chatId", AddingStates.SET_MONTH);
        calendar.set(Calendar.MONTH, LocalDateTime.now().plusMonths(Integer.parseInt(month) - 1).getMonthValue());
        int daysInMonth = YearMonth.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)).lengthOfMonth();
        String [] days = new String[daysInMonth];
        for (int i = 1; i <= daysInMonth; i++) {
            days[i - 1] = Integer.toString(i);
        }
        StringBuilder respond = new StringBuilder();
        respond.append("Choose day\r\n");
        for (int i = 0; i < days.length; i++){
           respond.append((i) + ". " + days[i] + "\r\n");
        }
        Assert.assertEquals(respond.toString(), output.toString());
        Assert.assertEquals(AddingStates.SET_DAY, state);
    }

}