import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.File;
import java.io.IOException;

public class JoeDuck {
    private static final String LINE_DIVIDER = "---";
    private static final String MOTD = "Welcome to Joe Duck";
    private static final String EXIT_MESSAGE = "Goodbye from Joe Duck";
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printResponse(MOTD);

        // try and find saved data
        String homePath = System.getProperty("user.home");
        Path dataFolderPath = Paths.get(homePath, "ip_data");
        if (!Files.exists(dataFolderPath)) {
            dataFolderPath.toFile().mkdirs();
        }
        Path dataFilePath = Paths.get(homePath, "ip_data", "tasks.txt");
        if (Files.exists(dataFilePath)) {
            printResponse("tasks.json found!");
        } else {
            try {
                dataFilePath.toFile().createNewFile();
            } catch (IOException e) {
                System.out.println("Exception: " + e);
                return;
            }
        }

        List<Task> inputs = new ArrayList<>();
        boolean endSession = false;

        while (scanner.hasNextLine()) {
            // TODO make special errors for every case :DDDD ?
            // TODO change to enum :DDDD ?
            try {
                String currInput = scanner.nextLine().trim();
                String currCommand = "";
                String commandPattern = "([a-zA-Z]+)";
                Pattern pp = Pattern.compile(commandPattern);
                Matcher mm = pp.matcher(currInput);
                if (mm.find()) {
                    currCommand = mm.group(1);
                }

                switch (currCommand) {
                    case "bye":
                        endSession = true;
                        break;
                    case "list":
                        printResponse(inputsToString(inputs, true));
                        break;
                    case "write":
                        printResponse(inputsToString(inputs, false));
                        try (PrintWriter out = new PrintWriter(dataFilePath.toString())) {
                            out.println(inputsToString(inputs, false));
                        }
                        break;
                    case "mark": {
                        String targetIndexStr = currInput.substring(5);
                        int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                        Task targetTask = inputs.get(targetIndex);
                        targetTask.setDoneStatus(true);
                        printResponse("Marked " + targetTask);
                        break;
                    }
                    case "unmark": {
                        String targetIndexStr = currInput.substring(7);
                        int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                        Task targetTask = inputs.get(targetIndex);
                        targetTask.setDoneStatus(false);
                        printResponse("Unmarked " + targetTask);
                        break;
                    }
                    case "delete": {
                        String targetIndexStr = currInput.substring(7);
                        int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                        Task targetTask = inputs.get(targetIndex);
                        inputs.remove(targetTask);
                        printResponse("Removed " + targetTask);
                        break;
                    }
                    case "todo":  // buggy
                        if (currInput.trim().length() <= 4) {
                            throw new EmptyTodoException("Your todoing description is empty, you buffoon");
                        }
                        String todoString = currInput.substring(5);
                        Todo t = new Todo(todoString);
                        inputs.add(t);
                        printResponse("Added Todo:\n" + t);
                        break;
                    case "deadline": {
                        String deadlineString = currInput.substring(9);
                        String pattern = "(.+) /by (.+)";
                        Pattern p = Pattern.compile(pattern);
                        Matcher m = p.matcher(deadlineString);
                        m.find(); //TODO fix another day

                        String desc = m.group(1);
                        String deadlineDate = m.group(2);
                        Deadline d = new Deadline(desc, deadlineDate);
                        inputs.add(d);
                        printResponse("Added Deadline:\n" + d);
                        break;
                    }
                    case "event": {
                        String deadlineString = currInput.substring(6);
                        String pattern = "(.+) /from (.+) /to (.+)";
                        Pattern p = Pattern.compile(pattern);
                        Matcher m = p.matcher(deadlineString);
                        m.find(); //TODO fix another day

                        String desc = m.group(1);
                        String eventStartDate = m.group(2);
                        String eventEndDate = m.group(3);
                        Event e = new Event(desc, eventStartDate, eventEndDate);
                        inputs.add(e);
                        printResponse("Added Event:\n" + e);
                        break;
                    }
                    default:
                        throw new InvalidCommandException(currInput);
                }
            } catch (JoeDuckException de) {
                printError("joeduck", de);
            } catch (Exception e) {
                printError(e);
            }

            if (endSession) {
                break;
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
        printResponse("Caught a " + desc + " pokemon:\n" + e);
    }

    private static void printError(Exception e) {
        printResponse("Caught an unknown pokemon:\n" + e);
    }

    private static String inputsToString(List<Task> list, boolean prependIndex) {
        StringBuilder ans = new StringBuilder();
        int count = 1;
        for (Task s : list) {
            if (prependIndex) {
                ans.append(count).append(". ");
            }
            ans.append(s);
            if (count < list.size()) {
                ans.append("\n");
            }
            count++;
        }
        return ans.toString();
    }
}
