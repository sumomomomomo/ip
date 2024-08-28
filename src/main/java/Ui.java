import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ui {
    private static final String LINE_DIVIDER = "---";
    private static final String MOTD = "Welcome to Joe Duck";
    private static final String EXIT_MESSAGE = "Goodbye from Joe Duck";
    public final Scanner scanner = new Scanner(System.in);

    public void printResponse(String res) {
        System.out.println(LINE_DIVIDER);
        System.out.println(res);
        System.out.println(LINE_DIVIDER);
    }

    public void printError(String msg) {
        printResponse("Error: " + msg);
    }

    public boolean scannerHasNextLine() {
        return scanner.hasNextLine();
    }

    public String scannerNextLine() {
        return scanner.nextLine();
    }

    public void onStart() {
        printResponse(MOTD);
    }

    public void onExit() {
        printResponse(EXIT_MESSAGE);
    }
}
