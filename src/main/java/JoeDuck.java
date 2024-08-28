import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class JoeDuck {
    private final Ui ui;
    private Storage storage;
    private TaskList tasks;
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

    private void run() {
        ui.onStart();
        boolean endSession = false;
        while (ui.scannerHasNextLine()) {
            try {
                Command currCommand = parser.parseUserInput(ui.scannerNextLine());
                switch (currCommand.getCommand()) {
                    case "bye", "exit":
                        endSession = true;
                        break;
                    case "list":
                        ui.printResponse(Utils.inputsToString(tasks.getTaskList(), true));
                        break;
                    case "mark": {
                        String targetIndexStr = currCommand.getArgs();
                        int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                        Task targetTask = tasks.getTask(targetIndex);
                        targetTask.setDoneStatus(true);
                        ui.printResponse("Marked " + targetTask);
                        storage.writeList(tasks.getTaskList());
                        break;
                    }
                    case "unmark": {
                        String targetIndexStr = currCommand.getArgs();
                        int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                        Task targetTask = tasks.getTask(targetIndex);
                        targetTask.setDoneStatus(false);
                        ui.printResponse("Unmarked " + targetTask);
                        storage.writeList(tasks.getTaskList());
                        break;
                    }
                    case "delete", "remove": {
                        String targetIndexStr = currCommand.getArgs();
                        int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                        Task targetTask = tasks.getTask(targetIndex);
                        tasks.removeTask(targetIndex);
                        ui.printResponse("Removed " + targetTask);
                        storage.writeList(tasks.getTaskList());
                        break;
                    }
                    case "todo":
                        String todoString = currCommand.getArgs();
                        Todo t = new Todo(todoString);
                        tasks.addTask(t);
                        ui.printResponse("Added Todo:\n" + t);
                        storage.writeList(tasks.getTaskList());
                        break;
                    case "deadline": {
                        String deadlineString = currCommand.getArgs();
                        String pattern = "(.+) /by (\\d{4}-\\d{2}-\\d{2}+) (\\d{2}:\\d{2})";
                        Pattern pd = Pattern.compile(pattern);
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
                        tasks.addTask(d);
                        ui.printResponse("Added Deadline:\n" + d);
                        storage.writeList(tasks.getTaskList());
                        break;
                    }
                    case "event": {
                        String deadlineString = currCommand.getArgs();
                        String pattern = "(.+) /from (\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2}) " +
                                "/to (\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2})";
                        Pattern pe = Pattern.compile(pattern);
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

            if (endSession) {
                break;
            }
        }
        ui.onExit();
    }

    public static void main(String[] args) {
        new JoeDuck().run();
    }
}
