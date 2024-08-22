import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JoeDuck {
    private static final String LINE_DIVIDER = "---";
    private static final String MOTD = "Welcome to Joe Duck";
    private static final String EXIT_MESSAGE = "Goodbye from Joe Duck";
    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        printResponse(MOTD);
        List<String> inputs = new ArrayList<>();

        while (true) {
            String currInput = scanner.nextLine();
            if (currInput.equals("bye")) {
                break;
            }
            else if (currInput.equals("list")) {
                printResponse(inputsToString((inputs)));
            }
            else {
                inputs.add(currInput);
                printResponse("Added: " + currInput);
            }
        }
        printResponse(EXIT_MESSAGE);
    }

    private static void printResponse(String res) {
        System.out.println(LINE_DIVIDER);
        System.out.println(res);
        System.out.println(LINE_DIVIDER);
    }

    private static String inputsToString(List<String> list) {
        StringBuilder ans = new StringBuilder();
        int count = 1;
        for (String s : list) {
            ans.append(count).append(". ").append(s);
            if (count < list.size()) {
                ans.append("\n");
            }
            count++;
        }
        return ans.toString();
    }
}
