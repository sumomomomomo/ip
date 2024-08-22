import java.util.Scanner;

public class JoeDuck {
    private static final String LINE_DIVIDER = "---";
    private static final String MOTD = "Welcome to Joe Duck";
    private static final String EXIT_MESSAGE = "Goodbye from Joe Duck";
    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        printResponse(MOTD);
//        while (true) {
//            String userName = scanner.nextLine();  // Read user input
//            System.out.println("You typed " + userName);  // Output user input
//        }
        printResponse(EXIT_MESSAGE);
    }

    private static void printResponse(String res) {
        System.out.println(LINE_DIVIDER);
        System.out.println(res);
    }
}
