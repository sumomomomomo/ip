package joeduck;

import joeduck.command.Command;
import joeduck.exception.InvalidCommandException;
import joeduck.exception.JoeDuckException;
import joeduck.exception.RegexMatchFailureException;
import joeduck.exception.StorageLoadException;
import joeduck.parser.Parser;
import joeduck.storage.Storage;
import joeduck.task.*;
import joeduck.ui.Ui;
import joeduck.utils.Utils;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main class. Interactive chatbot.
 */
public class JoeDuck {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private final Parser parser;

    private JoeDuck() {
        ui = new Ui();
        storage = new Storage();
        parser = new Parser();
        try {
            tasks = new TaskList();
            tasks.setTaskList(storage.getTasksFromFile());
        } catch (StorageLoadException e) {
            // cry
            throw new RuntimeException(e);
        }
    }

    private boolean executeCommand(Command currCommand) {
        try {
            switch (currCommand.command()) {
                case "bye", "exit":
                    return true;
                case "list":
                    ui.printResponse(Utils.inputsToString(tasks.getTaskList(), true));
                    break;
                case "mark": {
                    String targetIndexStr = currCommand.args();
                    int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                    Task targetTask = tasks.getTask(targetIndex);
                    targetTask.setDoneStatus(true);
                    ui.printResponse("Marked " + targetTask);
                    storage.writeList(tasks.getTaskList());
                    break;
                }
                case "unmark": {
                    String targetIndexStr = currCommand.args();
                    int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                    Task targetTask = tasks.getTask(targetIndex);
                    targetTask.setDoneStatus(false);
                    ui.printResponse("Unmarked " + targetTask);
                    storage.writeList(tasks.getTaskList());
                    break;
                }
                case "delete", "remove": {
                    String targetIndexStr = currCommand.args();
                    int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                    Task targetTask = tasks.getTask(targetIndex);
                    tasks.removeTask(targetIndex);
                    ui.printResponse("Removed " + targetTask);
                    storage.writeList(tasks.getTaskList());
                    break;
                }
                case "find": {
                    String keyword = currCommand.getArgs();
                    ui.printResponse(tasks.findTask(keyword));
                    break;
                }
                case "todo":
                    String todoString = currCommand.args();
                    if (todoString.isEmpty()) {
                        throw new InvalidCommandException("Todo requires a description.");
                    }
                    Todo t = new Todo(todoString);
                    tasks.addTask(t);
                    ui.printResponse("Added Todo:\n" + t);
                    storage.writeList(tasks.getTaskList());
                    break;
                case "deadline": {
                    String deadlineString = currCommand.args();
                    String pattern = "(.+) /by (\\d{4}-\\d{2}-\\d{2}+) (\\d{2}:\\d{2})";
                    Pattern pd = Pattern.compile(pattern);
                    Deadline d = getDeadline(pd, deadlineString);
                    tasks.addTask(d);
                    ui.printResponse("Added Deadline:\n" + d);
                    storage.writeList(tasks.getTaskList());
                    break;
                }
                case "event": {
                    String deadlineString = currCommand.args();
                    String pattern = "(.+) /from (\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2}) " +
                            "/to (\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2})";
                    Pattern pe = Pattern.compile(pattern);
                    Event e = getEvent(pe, deadlineString);
                    tasks.addTask(e);
                    ui.printResponse("Added Event:\n" + e);
                    storage.writeList(tasks.getTaskList());
                    break;
                }
                default:
                    throw new InvalidCommandException("Invalid command!");
            }
        } catch (JoeDuckException e) {
            ui.printError(e.getMessage());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private static Event getEvent(Pattern pe, String deadlineString) throws RegexMatchFailureException {
        Matcher me = pe.matcher(deadlineString);
        if (!me.find()) {
            throw new RegexMatchFailureException("Arguments for creating event is incorrect");
        }

        String desc = me.group(1);
        String startDate = me.group(2);
        LocalDate d1 = LocalDate.parse(startDate);
        String startTime = me.group(3);
        LocalTime t1 = LocalTime.parse(startTime);
        String endDate = me.group(4);
        LocalDate d2 = LocalDate.parse(endDate);
        String endTime = me.group(5);
        LocalTime t2 = LocalTime.parse(endTime);

        LocalDateTime startDt = LocalDateTime.of(d1, t1);
        LocalDateTime endDt = LocalDateTime.of(d2, t2);
        Event e = new Event(desc, startDt, endDt);
        return e;
    }

    private static Deadline getDeadline(Pattern pd, String deadlineString) throws RegexMatchFailureException {
        Matcher md = pd.matcher(deadlineString);
        if (!md.find()) {
            throw new RegexMatchFailureException("Arguments for creating deadline is incorrect");
        }

        String desc = md.group(1);
        String date = md.group(2);
        LocalDate localDate = LocalDate.parse(date);

        String time = md.group(3);
        LocalTime localTime = LocalTime.parse(time);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

        Deadline d = new Deadline(desc, localDateTime);
        return d;
    }

    private void run() {
        ui.onStart();
        boolean endSession = false;
        while (!endSession && ui.scannerHasNextLine()) {
            try {
                Command currCommand = parser.parseUserInput(ui.scannerNextLine());
                endSession = executeCommand(currCommand);
            } catch (InvalidCommandException e) {
                ui.printError(e.getMessage());
            }
        }
        ui.onExit();
    }

    public static void main(String[] args) {
        new JoeDuck().run();
    }
}
