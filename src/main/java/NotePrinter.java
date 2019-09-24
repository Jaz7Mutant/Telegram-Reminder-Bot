import java.util.TimerTask;

public class NotePrinter extends TimerTask {
    private String noteText;

    public NotePrinter(String noteText){
        this.noteText = noteText;
    }

    @Override
    public void run() {
        System.out.print(noteText);
    }
}
