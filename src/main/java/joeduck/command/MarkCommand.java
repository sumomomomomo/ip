package joeduck.command;

import java.io.FileNotFoundException;

import joeduck.JoeDuck;
import joeduck.task.Task;

/**
 * Marks an entry.
 */
public class MarkCommand extends Command {
    public MarkCommand(String args) {
        super("mark", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) throws FileNotFoundException {
        // Get entry
        int targetIndex = Integer.parseInt(getArgs()) - 1;
        Task targetTask = joeDuck.getTasks().getTask(targetIndex);
        // Update storage
        targetTask.setDoneStatus(true);
        joeDuck.getStorage().writeList(joeDuck.getTasks().getTaskList());
        return joeDuck.getUi().printResponse("Marked " + targetTask);
    }
}
