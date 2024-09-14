package joeduck;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import joeduck.command.Command;
import joeduck.exception.JoeDuckException;
import joeduck.exception.StorageLoadException;
import joeduck.parser.Parser;
import joeduck.storage.Storage;
import joeduck.task.TaskList;
import joeduck.ui.MainWindow;
import joeduck.ui.Ui;

/**
 * Main class. Interactive chatbot.
 */
public class JoeDuck extends Application {
    private static final String TITLE_BAR_LABEL = "Joe Duck";
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private final Parser parser;
    private boolean isStorageLoaded;

    /**
     * Constructor for singleton JoeDuck. Represents the chatbot instance.
     */
    public JoeDuck() {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/MainWindow.fxml"));
        ui = new Ui(fxmlLoader);
        storage = new Storage();
        parser = new Parser();
        tasks = new TaskList();
        try {
            tasks.setTaskList(storage.getTasksFromFile());
            isStorageLoaded = true;
        } catch (StorageLoadException e) {
            tasks.setTaskList(new ArrayList<>());
            isStorageLoaded = false;
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

    /**
     * Parses user's input and executes inputted commands.
     * @param input String input from User into the input box.
     * @return String output from command execution.
     */
    public String getResponse(String input) {
        Command currCommand = parser.parseUserInput(input);
        return executeCommand(currCommand);
    }

    private String executeCommand(Command currCommand) {
        try {
            return currCommand.execute(this);
        } catch (JoeDuckException | FileNotFoundException | NumberFormatException | IndexOutOfBoundsException e) {
            return ui.printError(e.getMessage());
        }
    }

    /**
     * Updates storage with current contents of TaskList.
     * If storage failed to load initially, does nothing.
     * @throws FileNotFoundException Thrown when storage fails to write.
     */
    public void updateStorage() throws FileNotFoundException {
        if (!isStorageLoaded) {
            return;
        }
        storage.writeList(tasks.getTaskList());
    }
    @Override
    public void start(Stage stage) {
        try {
            AnchorPane ap = ui.getFxmlLoader().load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle(TITLE_BAR_LABEL);
            ui.getFxmlLoader().<MainWindow>getController().setJoeDuck(this);
            stage.show();

            if (isStorageLoaded) {
                ui.displayResponse("Storage loaded successfully.");
            } else {
                ui.displayResponse("Failed to load storage.\nTask list is blank.\n"
                        + "All commands will not save to storage.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
