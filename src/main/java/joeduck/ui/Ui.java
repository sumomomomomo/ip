package joeduck.ui;

import java.util.Scanner;

/**
 * Handles user input/output.
 */
public class Ui {
    private static final String EXIT_MESSAGE = "Goodbye from Joe Duck";

    /**
     * Scanner to be used with Singleton Ui instance in JoeDuck.
     */
    public final Scanner scanner = new Scanner(System.in);

    /**
     * Prints the response, res.
     * Returns a String for compatibility with JavaFX.
     * @param res Response of JoeDuck.
     * @return res
     */
    public String printResponse(String res) {
        System.out.println(res);
        return res;
    }

    /**
     * Prints an error message.
     * Returns a String for compatibility with JavaFX.
     * @param msg Error message.
     * @return res
     */
    public String printError(String msg) {
        printResponse("Error: " + msg);
        return "Error: " + msg;
    }

    /**
     * Prints a goodbye message on exit.
     * Called on exit.
     * @return The goodbye message.
     */
    public String onExit() {
        printResponse(EXIT_MESSAGE);
        return EXIT_MESSAGE;
    }
}
