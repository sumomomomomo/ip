package joeduck;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import joeduck.command.Command;
import joeduck.exception.InvalidCommandException;
import joeduck.exception.JoeDuckException;
import joeduck.exception.RegexMatchFailureException;
import joeduck.exception.StorageLoadException;
import joeduck.parser.Parser;
import joeduck.storage.Storage;
import joeduck.task.Deadline;
import joeduck.task.Event;
import joeduck.task.Task;
import joeduck.task.TaskList;
import joeduck.task.Todo;
import joeduck.ui.MainWindow;
import joeduck.ui.Ui;
import joeduck.utils.Utils;

/**
 * Main class. Interactive chatbot.
 */
public class JoeDuck extends Application {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private final Parser parser;

    /**
     * Constructor for singleton JoeDuck. Represents the chatbot instance.
     */
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
    public TaskList getTasks() {
        return tasks;
    }
    public Storage getStorage() {
        return storage;
    }
    public Ui getUi() {
        return ui;
    }

    public Parser getParser() {
        return parser;
    }

    private String executeCommand(Command currCommand) {
        try {
            return currCommand.execute(this);
        } catch (JoeDuckException | FileNotFoundException e) {
            return ui.printError(e.getMessage());
        }
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
