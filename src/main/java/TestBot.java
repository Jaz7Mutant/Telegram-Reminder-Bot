import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestBot {
    @Test
    public void testEcho()
    {
        String echo = "echo";
        String someText = "someText";
        List<String> args = new ArrayList<String>();
        args.add(echo);
        args.add(someText);
        Bot bot = new Bot();
        String respond = bot.echo(args);
        Assert.assertEquals(someText+ " ", respond);
    }

    @Test
    public void testDate()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E HH:mm:ss zzz dd/MM/yyyy ");
        Date date = new Date();
        Bot bot = new Bot();
        Assert.assertEquals(dateFormat.format(date), bot.date());
    }

}
