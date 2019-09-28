import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Bot {
    private static String botHelp = "This is a simple chat bot." +
            "\r\nKeys: \r\n\t[-h], [--help] -- show help" +
            "\r\nFunctions:\r\n\thelp -- show help" +
            "\r\n\techo <args> -- print <args>" +
            "\r\n\tdate -- print current date and time" +
            "\r\n\tstop -- exit chat bot";
    private static String welcomeText = "Welcome. This is simple chat bot v0.3 alpha";
    private static String aboutText = "about"; //TODO вынести весь текст в json или отдельный класс
    private static UserIO userIO = new ConsoleIO();

    public static void main(String[] args) {
        userIO.showMessage(welcomeText);
        Map<String, Consumer<String>> commands = new HashMap<>();
        commands.put("-new", NoteMaker::addNote);
        commands.put("-remove", NoteMaker::removeNote);
        commands.put("-all", NoteMaker::showAllNotes);
        commands.put("-exit", Bot::exit);
        commands.put("-help", Bot::help);
        commands.put("-about", Bot::about);
        commands.put("-echo", Bot::echo);

        String currentCommand = "";
        while (!currentCommand.equals("-exit")) {
            currentCommand = userIO.getUserText(null);
            if (commands.containsKey(currentCommand.split(" ")[0])) {
                commands.get(currentCommand).accept(currentCommand);
            }
        }
    }

    private static void exit(String _s) {
        System.exit(0);
    }

    private static void help(String _s) {
        userIO.showMessage(botHelp);
    }

    private static void about(String _s) {
        userIO.showMessage(aboutText);
    }

    private static void echo(String s){
        userIO.showMessage(s.substring(5));
    }
}
