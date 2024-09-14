package joeduck.command;

import java.io.FileNotFoundException;

import joeduck.JoeDuck;
import joeduck.task.Task;

/**
 * Deletes an entry.
 */
public class DeleteCommand extends Command {
    public DeleteCommand(String args) {
        super("delete", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) throws FileNotFoundException {
        // Get the task by index
        int targetIndex = Integer.parseInt(getArgs()) - 1;
        Task targetTask = joeDuck.getTasks().getTask(targetIndex);
        // Remove task and update storage
        joeDuck.getTasks().removeTask(targetIndex);
        joeDuck.getStorage().writeList(joeDuck.getTasks().getTaskList());
        return joeDuck.getUi().printResponse("Removed " + targetTask);
    }
}
