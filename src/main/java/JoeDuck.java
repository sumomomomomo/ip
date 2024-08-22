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
        List<Task> inputs = new ArrayList<>();

        while (true) {
            String currInput = scanner.nextLine();
            if (currInput.equals("bye")) {
                break;
            }
            else if (currInput.equals("list")) {
                printResponse(inputsToString((inputs)));
            }
            else if (currInput.startsWith("mark ")) {
                String targetIndexStr = currInput.substring(5);
                try {
                    int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                    Task targetTask = inputs.get(targetIndex);
                    targetTask.setDoneStatus(true);
                    printResponse(targetTask.toString());
                }
                catch (Exception e) {
                    printResponse("Caught a pokemon when marking: " + e);
                }
            }
            else if (currInput.startsWith("unmark ")) {
                String targetIndexStr = currInput.substring(7);
                try {
                    int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                    Task targetTask = inputs.get(targetIndex);
                    targetTask.setDoneStatus(false);
                    printResponse(targetTask.toString());
                }
                catch (Exception e) {
                    printResponse("Caught a pokemon when unmarking: " + e);
                }
            }
            else {
                inputs.add(new Task(currInput));
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

    private static String inputsToString(List<Task> list) {
        StringBuilder ans = new StringBuilder();
        int count = 1;
        for (Task s : list) {
            ans.append(count).append(". ").append(s);
            if (count < list.size()) {
                ans.append("\n");
            }
            count++;
        }
        return ans.toString();
    }
}
