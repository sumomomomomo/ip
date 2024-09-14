package joeduck.command;

import joeduck.JoeDuck;
import joeduck.task.Task;

import java.io.FileNotFoundException;

public class DeleteCommand extends Command {
    public DeleteCommand(String args) {
        super("delete", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) throws FileNotFoundException {
        int targetIndex = Integer.parseInt(getArgs()) - 1;
        Task targetTask = joeDuck.getTasks().getTask(targetIndex);
        joeDuck.getTasks().removeTask(targetIndex);
        joeDuck.getStorage().writeList(joeDuck.getTasks().getTaskList());
        return joeDuck.getUi().printResponse("Removed " + targetTask);
    }
}