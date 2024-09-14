package joeduck.command;

import joeduck.JoeDuck;
import joeduck.exception.InvalidCommandException;
import joeduck.task.Todo;

import java.io.FileNotFoundException;

public class TodoCommand extends Command {
    public TodoCommand(String args) {
        super("todo", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) throws InvalidCommandException, FileNotFoundException {
        if (getArgs().isEmpty()) {
            throw new InvalidCommandException("Todo requires a description.");
        }
        Todo t = new Todo(getArgs());
        joeDuck.getTasks().addTask(t);
        joeDuck.getStorage().writeList(joeDuck.getTasks().getTaskList());
        return joeDuck.getUi().printResponse("Added Todo:\n" + t);
    }
}