package joeduck.command;

import joeduck.JoeDuck;
import joeduck.task.Task;

import java.io.FileNotFoundException;

public class MarkCommand extends Command {
    public MarkCommand(String args) {
        super("mark", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) throws FileNotFoundException {
        int targetIndex = Integer.parseInt(getArgs()) - 1;
        Task targetTask = joeDuck.getTasks().getTask(targetIndex);
        targetTask.setDoneStatus(true);
        joeDuck.getStorage().writeList(joeDuck.getTasks().getTaskList());
        return joeDuck.getUi().printResponse("Marked " + targetTask);
    }
}
