import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class JoeDuck {
    private static final String LINE_DIVIDER = "---";
    private static final String MOTD = "Welcome to Joe Duck";
    private static final String EXIT_MESSAGE = "Goodbye from Joe Duck";
    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        printResponse(MOTD);
        List<Task> inputs = new ArrayList<>();

        while (true) {
            // TODO make startsWith equal: some have spaces, some do not
            // at the end of the string they are checking for
            try {
                String currInput = scanner.nextLine();
                if (currInput.equals("bye")) {
                    break;
                } else if (currInput.equals("list")) {
                    printResponse(inputsToString((inputs)));
                } else if (currInput.startsWith("mark ")) {
                    String targetIndexStr = currInput.substring(5);
                    try {
                        int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                        Task targetTask = inputs.get(targetIndex);
                        targetTask.setDoneStatus(true);
                        printResponse(targetTask.toString());
                    } catch (Exception e) {
                        printError("mark", e);
                    }
                } else if (currInput.startsWith("unmark ")) {
                    String targetIndexStr = currInput.substring(7);
                    try {
                        int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                        Task targetTask = inputs.get(targetIndex);
                        targetTask.setDoneStatus(false);
                        printResponse(targetTask.toString());
                    } catch (Exception e) {
                        printError("unmark", e);
                    }
                } else if (currInput.startsWith("todo")) {
                    try {
                        if (currInput.length() <= 5) {
                            throw new EmptyTodoException("Your todoing description is empty, you buffoon");
                        }
                        String todoString = currInput.substring(5);
                        Todo t = new Todo(todoString);
                        inputs.add(t);
                        printResponse("Added Todo:\n" + t);
                    } catch (Exception e) {
                        printError("todo", e);
                    }
                } else if (currInput.startsWith("deadline ")) {
                    String deadlineString = currInput.substring(9);
                    String pattern = "(.+) \\/by (.+)";
                    try {
                        Pattern p = Pattern.compile(pattern);
                        Matcher m = p.matcher(deadlineString);
                        m.find(); //TODO fix another day
                        String desc = m.group(1);
                        String deadlineDate = m.group(2);
                        Deadline d = new Deadline(desc, deadlineDate);
                        inputs.add(d);
                        printResponse("Added Deadline:\n" + d);
                    } catch (Exception e) {
                        printError("deadline", e);
                    }
                } else if (currInput.startsWith("event ")) {
                    String deadlineString = currInput.substring(6);
                    String pattern = "(.+)\\/from (.+) \\/to (.+)";
                    try {
                        Pattern p = Pattern.compile(pattern);
                        Matcher m = p.matcher(deadlineString);
                        m.find(); //TODO fix another day
                        String desc = m.group(1);
                        String eventStartDate = m.group(2);
                        String eventEndDate = m.group(3);
                        Event e = new Event(desc, eventStartDate, eventEndDate);
                        inputs.add(e);
                        printResponse("Added Event:\n" + e);
                    } catch (Exception e) {
                        printError("event", e);
                    }
                } else {
                    throw new InvalidCommandException(currInput);
                }
            }
            catch (JoeDuckException de) {
                printError("joeduck", de);
            }
        }
        printResponse(EXIT_MESSAGE);
    }

    private static void printResponse(String res) {
        System.out.println(LINE_DIVIDER);
        System.out.println(res);
        System.out.println(LINE_DIVIDER);
    }

    private static void printError(String desc, Exception e) {
        printResponse("Caught a pokemon when " + desc + ":\n" + e);
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
