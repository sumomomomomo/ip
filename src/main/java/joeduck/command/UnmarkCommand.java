package joeduck.command;

import joeduck.JoeDuck;
import joeduck.task.Task;

import java.io.FileNotFoundException;

public class UnmarkCommand extends Command {
    public UnmarkCommand(String args) {
        super("unmark", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) throws FileNotFoundException {
        int targetIndex = Integer.parseInt(getArgs()) - 1;
        Task targetTask = joeDuck.getTasks().getTask(targetIndex);
        targetTask.setDoneStatus(false);
        joeDuck.getStorage().writeList(joeDuck.getTasks().getTaskList());
        return joeDuck.getUi().printResponse("Unmarked " + targetTask);
    }
}