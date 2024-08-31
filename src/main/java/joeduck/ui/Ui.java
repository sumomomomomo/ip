package joeduck.ui;

import java.util.Scanner;

/**
 * Handles user input/output.
 */
public class Ui {
    private static final String MOTD = "Welcome to Joe Duck";
    private static final String EXIT_MESSAGE = "Goodbye from Joe Duck";

    /**
     * Scanner to be used with Singleton Ui instance in JoeDuck.
     */
    public final Scanner scanner = new Scanner(System.in);

    public String printResponse(String res) {
        System.out.println(res);
        return res;
    }

    public String printError(String msg) {
        printResponse("Error: " + msg);
        return "Error: " + msg;
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

    public String onExit() {
        printResponse(EXIT_MESSAGE);
        return EXIT_MESSAGE;
    }
}
