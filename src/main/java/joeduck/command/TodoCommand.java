package joeduck.command;

import java.io.FileNotFoundException;

import joeduck.JoeDuck;
import joeduck.exception.InvalidCommandException;
import joeduck.task.Todo;

/**
 * Creates a Todo.
 */
public class TodoCommand extends Command {
    public TodoCommand(String args) {
        super("todo", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) throws InvalidCommandException, FileNotFoundException {
        // Get details
        if (getArgs().isEmpty()) {
            throw new InvalidCommandException("Todo requires a description.");
        }
        Todo t = new Todo(getArgs());
        // Add task
        joeDuck.getTasks().addTask(t);
        joeDuck.getStorage().writeList(joeDuck.getTasks().getTaskList());
        return joeDuck.getUi().printResponse("Added Todo:\n" + t);
    }
}