import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestBot {

    //public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOut = System.out;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(output));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testEcho() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Bot bot = new Bot();
        Method method = Bot.class.getDeclaredMethod("echo", String.class);
        method.setAccessible(true);
        method.invoke((Object)bot, (Object)"-echo test");
        Assert.assertEquals("test", output.toString());
    }

    @Test
    public void testDate() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Bot bot = new Bot();
        Method method = Bot.class.getDeclaredMethod("date", String.class);
        method.setAccessible(true);
        method.invoke((Object)bot, (Object)"-date");
        Assert.assertEquals(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                output.toString());
    }

}


