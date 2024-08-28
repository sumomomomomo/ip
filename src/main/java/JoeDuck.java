import java.io.FileNotFoundException;
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
    private static final String LOAD_REGEX_PATTERN = "^\\[(.)]\\[([ |X])] (.+)$";
    private static final String MOTD = "Welcome to Joe Duck";
    private static final String EXIT_MESSAGE = "Goodbye from Joe Duck";
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        List<Task> inputs = new ArrayList<>();

        printResponse(MOTD);

        // try and find saved data
        // TODO refactor regex so compile isn't spammed everywhere
        // TODO add error handling for invalid save files
        String homePath = System.getProperty("user.home");
        Path dataFolderPath = Paths.get(homePath, "ip_data");
        if (!Files.exists(dataFolderPath)) {
            dataFolderPath.toFile().mkdirs();
        }
        Path dataFilePath = Paths.get(homePath, "ip_data", "tasks.txt");
        if (Files.exists(dataFilePath)) {
            printResponse("tasks.json found!");
            // load saved data
            try {
                Scanner s = new Scanner(dataFilePath.toFile());
                Pattern p = Pattern.compile(LOAD_REGEX_PATTERN);
                while (s.hasNextLine()) {
                    String currLine = s.nextLine().trim();
                    Matcher m = p.matcher(currLine);
                    m.find();

                    String type = m.group(1);
                    String done = m.group(2);
                    boolean doneStatus = done.equals(Task.DONE_ICON);
                    String descAndMisc = m.group(3);

                    switch (type) {
                        case "T":
                            Todo currTodo = new Todo(descAndMisc);
                            currTodo.setDoneStatus(doneStatus);
                            inputs.add(currTodo);
                            break;
                        case "D":
                            Pattern pd = Pattern.compile(Deadline.REGEX_PATTERN);
                            Matcher md = pd.matcher(descAndMisc);
                            md.find();

                            Deadline currDeadline = new Deadline(md.group(1), md.group(2));
                            currDeadline.setDoneStatus(doneStatus);
                            inputs.add(currDeadline);
                            break;
                        case "E":
                            Pattern pe = Pattern.compile(Event.REGEX_PATTERN);
                            Matcher me = pe.matcher(descAndMisc);
                            me.find();

                            Event currEvent = new Event(me.group(1), me.group(2), me.group(3));
                            currEvent.setDoneStatus(doneStatus);
                            inputs.add(currEvent);
                            break;
                    }

                }
                s.close();
                printResponse("Tasks loaded successfully.");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                dataFilePath.toFile().createNewFile();
            } catch (IOException e) {
                System.out.println("Exception: " + e);
                return;
            }
        }
        boolean endSession = false;
        // main loop
        while (scanner.hasNextLine()) {
            // TODO make special errors for every case :DDDD ?
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
                        printResponse("Forced a write to file.");
                        writeList(inputs, dataFilePath);
                        break;
                    case "mark": {
                        String targetIndexStr = currInput.substring(5);
                        int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                        Task targetTask = inputs.get(targetIndex);
                        targetTask.setDoneStatus(true);
                        printResponse("Marked " + targetTask);
                        writeList(inputs, dataFilePath);
                        break;
                    }
                    case "unmark": {
                        String targetIndexStr = currInput.substring(7);
                        int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                        Task targetTask = inputs.get(targetIndex);
                        targetTask.setDoneStatus(false);
                        printResponse("Unmarked " + targetTask);
                        writeList(inputs, dataFilePath);
                        break;
                    }
                    case "delete": {
                        String targetIndexStr = currInput.substring(7);
                        int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                        Task targetTask = inputs.get(targetIndex);
                        inputs.remove(targetTask);
                        printResponse("Removed " + targetTask);
                        writeList(inputs, dataFilePath);
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
                        writeList(inputs, dataFilePath);
                        break;
                    case "deadline": {
                        String deadlineString = currInput.substring(9);
                        String pattern = "(.+) /by (.+)";
                        Pattern p = Pattern.compile(pattern);
                        Matcher m = p.matcher(deadlineString);
                        m.find();

                        String desc = m.group(1);
                        String deadlineDate = m.group(2);
                        Deadline d = new Deadline(desc, deadlineDate);
                        inputs.add(d);
                        printResponse("Added Deadline:\n" + d);
                        writeList(inputs, dataFilePath);
                        break;
                    }
                    case "event": {
                        String deadlineString = currInput.substring(6);
                        String pattern = "(.+) /from (.+) /to (.+)";
                        Pattern p = Pattern.compile(pattern);
                        Matcher m = p.matcher(deadlineString);
                        m.find();

                        String desc = m.group(1);
                        String eventStartDate = m.group(2);
                        String eventEndDate = m.group(3);
                        Event e = new Event(desc, eventStartDate, eventEndDate);
                        inputs.add(e);
                        printResponse("Added Event:\n" + e);
                        writeList(inputs, dataFilePath);
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

    private static void writeList(List<Task> list, Path dataFilePath) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(dataFilePath.toString())) {
            out.println(inputsToString(list, false));
        }
    }
}
