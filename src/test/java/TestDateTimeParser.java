import inputOutput.ConsoleIO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import reminder.AddingStates;
import reminder.DateTimeParser;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TestDateTimeParser {
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

    @Test
    public void testSetWrongYear() {
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
        Assert.assertArrayEquals(states, new AddingStates[]{state, state1, state2});
    }

    @Test
    public void testSetYear() {
        DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
        dateTimeParser.updateCurrentDate();
        AddingStates state = dateTimeParser.setYear(new GregorianCalendar(), Integer.toString(dateTimeParser.years.length - 1),
                "chatId", AddingStates.SET_YEAR);
        Assert.assertEquals("Set year (yyyy)\r\n", output.toString());
        AddingStates state1 = dateTimeParser.setYear(new GregorianCalendar(), "1",
                "chatId", AddingStates.SET_YEAR);
        AddingStates state2 = dateTimeParser.setYear(new GregorianCalendar(), "1",
                "chatId", AddingStates.SET_REMIND_YEAR);
        Assert.assertEquals(AddingStates.SET_MONTH, state1);
        Assert.assertEquals(AddingStates.SET_REMIND_MONTH, state2);
    }

    @Test
    public void testSetWrongMonth() {
        DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
        dateTimeParser.updateCurrentDate();
        AddingStates state = dateTimeParser.setMonth(new GregorianCalendar(), "",
                "chatId", AddingStates.SET_MONTH);
        AddingStates state1 = dateTimeParser.setMonth(new GregorianCalendar(), "-1",
                "chatId", AddingStates.SET_MONTH);
        AddingStates state2 = dateTimeParser.setMonth(new GregorianCalendar(), "13",
                "chatId", AddingStates.SET_MONTH);
        Assert.assertEquals("Wrong format\r\nWrong format\r\nWrong format\r\n", output.toString());
        AddingStates[] states = new AddingStates[3];
        Arrays.fill(states, AddingStates.SET_MONTH);
        Assert.assertArrayEquals(states, new AddingStates[]{state, state1, state2});
    }

    @Test
    public void testSetMonth() {
        DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
        dateTimeParser.updateCurrentDate();
        Calendar calendar = new GregorianCalendar();
        String month = "1";
        AddingStates state = dateTimeParser.setMonth(calendar, month,
                "chatId", AddingStates.SET_MONTH);
        calendar.set(Calendar.MONTH, LocalDateTime.now().plusMonths(Integer.parseInt(month) - 1).getMonthValue());
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        String[] days = new String[daysInMonth];
        for (int i = 1; i <= daysInMonth; i++) {
            days[i - 1] = Integer.toString(i);
        }
        StringBuilder respond = new StringBuilder();
        respond.append("Choose day\r\n");
        for (int i = 0; i < days.length; i++) {
            respond.append((i + 1) + ". " + days[i] + "\r\n");
        }
        Assert.assertEquals(respond.toString(), output.toString());
        AddingStates state1 = dateTimeParser.setMonth(calendar, month,
                "chatId", AddingStates.SET_REMIND_MONTH);
        Assert.assertEquals(AddingStates.SET_DAY, state);
        Assert.assertEquals(AddingStates.SET_REMIND_DAY, state1);
    }

    @Test
    public void testSetWrongDay() {
        DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
        dateTimeParser.updateCurrentDate();
        Calendar calendar = new GregorianCalendar();
        String month = "1";
        dateTimeParser.setMonth(calendar, month, "chatId", AddingStates.SET_MONTH);
        output = new ByteArrayOutputStream();
        setUpStreams();
        dateTimeParser.setDay(calendar, "32", "chatId", AddingStates.SET_DAY);
        dateTimeParser.setDay(calendar, "-1", "chatId", AddingStates.SET_DAY);
        Assert.assertEquals("Wrong format\r\nWrong format\r\n", output.toString());
    }

    @Test
    public void testSetDay() {
        DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
        dateTimeParser.updateCurrentDate();
        Calendar calendar = new GregorianCalendar();
        String month = "1";
        dateTimeParser.setMonth(calendar, month, "chatId", AddingStates.SET_MONTH);
        output = new ByteArrayOutputStream();
        setUpStreams();
        AddingStates state = dateTimeParser.setDay(calendar, "1", "chatId", AddingStates.SET_DAY);
        Assert.assertEquals("Set time (hh:mm)\r\n", output.toString());
        Assert.assertEquals(AddingStates.SET_TIME, state);
        AddingStates state1 = dateTimeParser.setDay(calendar, "1", "chatId", AddingStates.SET_REMIND_DAY);
        Assert.assertEquals(AddingStates.SET_REMIND_TIME, state1);
    }

    @Test
    public void testSetWrongTime(){
        DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
        dateTimeParser.updateCurrentDate();
        Calendar calendar = new GregorianCalendar();
        AddingStates state = dateTimeParser.setTime(calendar,"12 12",
                "chatId", AddingStates.SET_TIME);
        AddingStates state1 = dateTimeParser.setTime(calendar,"dfsjm",
                "chatId", AddingStates.SET_TIME);
        AddingStates state2 = dateTimeParser.setTime(calendar,"",
                "chatId", AddingStates.SET_TIME);
        Assert.assertEquals("Wrong format\r\nWrong format\r\nWrong format\r\n", output.toString());
        Assert.assertEquals(AddingStates.SET_TIME, state);
        Assert.assertEquals(AddingStates.SET_TIME, state1);
        Assert.assertEquals(AddingStates.SET_TIME, state2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");
        AddingStates state3 = dateTimeParser.setTime(calendar,LocalDateTime.now().minusHours(5).format(formatter),
                "chatId", AddingStates.SET_TIME);
        AddingStates state4 = dateTimeParser.setTime(calendar,LocalDateTime.now().minusHours(5).format(formatter),
                "chatId", AddingStates.SET_REMIND_TIME);
        Assert.assertEquals(AddingStates.SET_DAY, state3);
        Assert.assertEquals(AddingStates.SET_REMIND_DAY, state4);
        Assert.assertTrue(output.toString().contains("Wrong date\r\n"));
    }

    @Test
    public void testSetTime(){
        DateTimeParser dateTimeParser = new DateTimeParser(new ConsoleIO());
        dateTimeParser.updateCurrentDate();
        Calendar calendar = new GregorianCalendar(2019,Calendar.getInstance().get(Calendar.MONTH) + 1,
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        AddingStates state = dateTimeParser.setTime(calendar,
                ZonedDateTime.now().plusHours(5).format(DateTimeFormatter.ofPattern("hh:mm")),
                "chatId", AddingStates.SET_TIME);
        AddingStates state1 = dateTimeParser.setTime(calendar,
                ZonedDateTime.now().plusHours(5).format(DateTimeFormatter.ofPattern("hh:mm")),
                "chatId", AddingStates.SET_REMIND_TIME);
        Assert.assertEquals(AddingStates.SET_REMIND, state);
        Assert.assertEquals(AddingStates.IDLE, state1);
    }
}