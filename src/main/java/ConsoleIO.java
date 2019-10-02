import java.io.*;
import java.util.Scanner;

public class ConsoleIO implements UserIO {

    @Override
    public void showMessage(String message){
        System.out.print(message);
    }

    @Override
    public String getUserText(String prompt) {
        System.out.println(prompt);
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }

    @Override
    public int getOnClickButton(String[] buttons) {
        throw new UnsupportedOperationException(); //TODO;
    }

    @Override
    public void showList(String prompt, String[] elements) {
        throw new UnsupportedOperationException(); //TODO;
    }

    @Override
    public String getUserId() {
        return null;
    }
}
