package joeduck.command;

import java.io.FileNotFoundException;

import joeduck.JoeDuck;
import joeduck.task.Task;

/**
 * Unmarks an entry.
 */
public class UnmarkCommand extends Command {
    public UnmarkCommand(String args) {
        super("unmark", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) throws FileNotFoundException {
        // Edit task
        int targetIndex = Integer.parseInt(getArgs()) - 1;
        Task targetTask = joeDuck.getTasks().getTask(targetIndex);
        targetTask.setDoneStatus(false);
        // Update storage
        joeDuck.getStorage().writeList(joeDuck.getTasks().getTaskList());
        return joeDuck.getUi().printResponse("Unmarked " + targetTask);
    }
}