package joeduck;

import javafx.application.Platform;
import joeduck.command.Command;
import joeduck.exception.InvalidCommandException;
import joeduck.exception.JoeDuckException;
import joeduck.exception.RegexMatchFailureException;
import joeduck.exception.StorageLoadException;
import joeduck.parser.Parser;
import joeduck.storage.Storage;
import joeduck.task.*;
import joeduck.ui.MainWindow;
import joeduck.ui.Ui;
import joeduck.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Main class. Interactive chatbot.
 */
public class JoeDuck extends Application {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private final Parser parser;

    public JoeDuck() {
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

    private String executeCommand(Command currCommand) {
        try {
            switch (currCommand.command()) {
                case "bye", "exit":
                    Platform.exit();
                    return ui.onExit();
                case "list":
                    return ui.printResponse(Utils.inputsToString(tasks.getTaskList(), true));
                case "mark": {
                    String targetIndexStr = currCommand.args();
                    int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                    Task targetTask = tasks.getTask(targetIndex);
                    targetTask.setDoneStatus(true);
                    storage.writeList(tasks.getTaskList());
                    return ui.printResponse("Marked " + targetTask);
                }
                case "unmark": {
                    String targetIndexStr = currCommand.args();
                    int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                    Task targetTask = tasks.getTask(targetIndex);
                    targetTask.setDoneStatus(false);
                    storage.writeList(tasks.getTaskList());
                    return ui.printResponse("Unmarked " + targetTask);
                }
                case "delete", "remove": {
                    String targetIndexStr = currCommand.args();
                    int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                    Task targetTask = tasks.getTask(targetIndex);
                    tasks.removeTask(targetIndex);
                    storage.writeList(tasks.getTaskList());
                    return ui.printResponse("Removed " + targetTask);
                }
                case "find": {
                    String keyword = currCommand.args();
                    return ui.printResponse(tasks.findTask(keyword));
                }
                case "todo":
                    String todoString = currCommand.args();
                    if (todoString.isEmpty()) {
                        throw new InvalidCommandException("Todo requires a description.");
                    }
                    Todo t = new Todo(todoString);
                    tasks.addTask(t);
                    storage.writeList(tasks.getTaskList());
                    return ui.printResponse("Added Todo:\n" + t);
                case "deadline": {
                    String deadlineString = currCommand.args();
                    String pattern = "(.+) /by (\\d{4}-\\d{2}-\\d{2}+) (\\d{2}:\\d{2})";
                    Pattern pd = Pattern.compile(pattern);
                    Deadline d = getDeadline(pd, deadlineString);
                    tasks.addTask(d);
                    storage.writeList(tasks.getTaskList());
                    return ui.printResponse("Added Deadline:\n" + d);
                }
                case "event": {
                    String deadlineString = currCommand.args();
                    String pattern = "(.+) /from (\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2}) " +
                            "/to (\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2})";
                    Pattern pe = Pattern.compile(pattern);
                    Event e = getEvent(pe, deadlineString);
                    tasks.addTask(e);
                    storage.writeList(tasks.getTaskList());
                    return ui.printResponse("Added Event:\n" + e);
                }
                default:
                    throw new InvalidCommandException("Invalid command!");
            }
        } catch (JoeDuckException e) {
            return ui.printError(e.getMessage());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
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
        return new Event(desc, startDt, endDt);
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

        return new Deadline(desc, localDateTime);
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setJoeDuck(this);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getResponse(String input) {
        try {
            Command currCommand = parser.parseUserInput(input);
            return executeCommand(currCommand);
        } catch (InvalidCommandException e) {
            throw new RuntimeException(e);
        }
    }
}
