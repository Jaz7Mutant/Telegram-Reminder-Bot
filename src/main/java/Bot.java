import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Bot {
    private static String botHelp = "This is a bot-reminder." +
            "\r\nKeys: \r\n\t[-h], [--help] -- show help" +
            "\r\nFunctions:\r\n\thelp -- show help" +
            "\r\n\techo <args> -- print <args>" +
            "\r\n\tdate -- print current date and time" +
            "\r\n\tnew -- create new note" +
            "\r\n\tremove -- remove note" +
            "\r\n\tall -- show all your notes" +
            "\r\n\tstop -- exit chat bot";
    private static String welcomeText = "Welcome. This is bot-reminder v0.3 alpha";
    private static String authors = "Tolstoukhov Daniil, Gorbunova Sofia, 2019"; //TODO вынести весь текст в json или отдельный класс
    private static UserIO userIO = new ConsoleIO();

    public static void main(String[] args) {
        userIO.showMessage(welcomeText);
        Map<String, Consumer<String>> commands = new HashMap<>();
        commands.put("-new", NoteMaker::addNote);
        commands.put("-remove", NoteMaker::removeNote);
        commands.put("-all", NoteMaker::showAllNotes);
        commands.put("-exit", Bot::exit);
        commands.put("-help", Bot::help);
        commands.put("-authors", Bot::authors);
        commands.put("-echo", Bot::echo);
        commands.put("-date", Bot::date);

        String currentCommand = "";
        while (!currentCommand.equals("-exit")) {
            currentCommand = userIO.getUserText("");
            if (commands.containsKey(currentCommand.split(" ")[0])) {
                commands.get(currentCommand.split(" ")[0]).accept(currentCommand);
            }
        }
    }

    private static void exit(String _s) {
        System.exit(0);
    }

    private static void help(String _s) {
        userIO.showMessage(botHelp);
    }

    private static void authors(String _s) {
        userIO.showMessage(authors);
    }

    private static void date(String _s) {
        userIO.showMessage(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    private static void echo(String s){
        userIO.showMessage(s.substring(6));
    }
}
