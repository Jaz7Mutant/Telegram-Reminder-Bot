import bot.BotController;
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

public class TestBotController {

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
        BotController botController = new BotController();
        Method method = BotController.class.getDeclaredMethod("echo", String.class);
        method.setAccessible(true);
        method.invoke((Object) botController, (Object)"-echo test");
        Assert.assertEquals("test", output.toString());
    }

    @Test
    public void testDate() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BotController botController = new BotController();
        Method method = BotController.class.getDeclaredMethod("date", String.class);
        method.setAccessible(true);
        method.invoke((Object) botController, (Object)"-date");
        Assert.assertEquals(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                output.toString());
    }

}


