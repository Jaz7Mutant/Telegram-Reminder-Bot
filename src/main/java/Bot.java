import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Supplier;

public class Bot {
    private static String botHelp = "This is a simple chat bot." +
            "\r\nKeys: \r\n\t[-h], [--help] -- show help" +
            "\r\nFunctions:\r\n\thelp -- show help" +
            "\r\n\techo <args> -- print <args>" +
            "\r\n\tdate -- print current date and time" +
            "\r\n\tstop -- exit chat bot";

    private static String welcomeText = "Welcome. This is simple chat bot v0.2";

    private static String aboutText = "about"; //TODO
    // TODO вынести весь текст в json или отдельный класс

    public static void main(String[] args) {
        System.out.println(welcomeText);
        Map<String, Supplier<String>> commands = new HashMap<>();
        commands.put("-new", NoteMaker::addNote);
        commands.put("-remove", NoteMaker::removeNote);
        commands.put("-all", NoteMaker::showAllNotes); // Здесь просто добавляем все возможные функции.
        commands.put("-exit", Bot::exit);
        commands.put("-help", Bot::help);
        commands.put("-about", Bot::about);

        Scanner in = new Scanner(System.in);
        String currentCommand = in.nextLine();
        while (true) {
            if (commands.containsKey(currentCommand)) {
                System.out.println(commands.get(currentCommand).get());
            }
            currentCommand = in.nextLine();
        }
    }

    private static String exit() {
        System.exit(0);
        return null; // Только потому что все остальные методы в словаре должны возвращать значения.
    }

    private static String help() {
        return botHelp;
    }

    private static String about() {
        return aboutText;
    }
}
