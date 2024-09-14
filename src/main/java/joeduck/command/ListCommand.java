package joeduck.command;

import joeduck.JoeDuck;
import joeduck.utils.Utils;

/**
 * Gets a list of all commands.
 */
public class ListCommand extends Command {
    public ListCommand(String args) {
        super("list", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) {
        return joeDuck.getUi().printResponse(
                Utils.inputsToString(joeDuck.getTasks().getTaskList(), true));
    }
}
