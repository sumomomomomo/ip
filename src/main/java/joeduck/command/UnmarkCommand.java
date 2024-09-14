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
        // Get entry
        int targetIndex = Integer.parseInt(getArgs()) - 1;
        Task targetTask = joeDuck.getTasks().getTask(targetIndex);
        // Update storage
        targetTask.setDoneStatus(false);
        joeDuck.getStorage().writeList(joeDuck.getTasks().getTaskList());
        return joeDuck.getUi().printResponse("Unmarked " + targetTask);
    }
}