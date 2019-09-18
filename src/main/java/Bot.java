import java.text.SimpleDateFormat;
import java.util.*;

public class Bot
{
    private static String botHelp = "This is a simple chat bot." +
            "\r\nKeys: \r\n\t[-h], [--help] -- show help" +
            "\r\nFunctions:\r\n\thelp -- show help" +
            "\r\n\techo <args> -- print <args>" +
            "\r\n\tdate -- print current date and time" +
            "\r\n\tstop -- exit chat bot";

    public static void main(String[] args)
    {
        String welcome = "Welcome. This is simple chat bot v0.1";
        if (args.length > 1)
        {
            System.out.println(args[0]);
            System.out.println(args[1]);
            parseArguments(args);
        }
        Scanner in = new Scanner(System.in);
        System.out.println(welcome);
        List<String> currentCommand = Arrays.asList(in.nextLine().split(" "));
        String respond = "";
        while (true)
        {
            switch (currentCommand.get(0))
            {
                case "echo":
                    respond = echo(currentCommand);
                    break;
                case "date":
                    respond = date();
                    break;
                case "help":
                    respond = botHelp;
                    break;
                case "stop":
                    return;
            }
            System.out.println(respond);
            respond = "";
            currentCommand = Arrays.asList(in.nextLine().split(" "));
        }
    }

    public static String echo(List<String> currentCommand)
    {
        String respond = "";
        for (int i = 1; i < currentCommand.size(); i++)
        {
            respond += currentCommand.get(i) + " ";
        }
        return respond;
    }

    public static String date()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E HH:mm:ss zzz dd/MM/yyyy ");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String botHelp()
    {
        return botHelp;
    }


    private static void parseArguments(String[] args)
    {
        switch (args[1])
        {
            case "-h":
            case"--help":
                System.out.println(botHelp);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }
}
