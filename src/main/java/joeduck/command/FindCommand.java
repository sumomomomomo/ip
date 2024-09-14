package joeduck.command;

import joeduck.JoeDuck;

/**
 * Finds all commands that match a regex pattern.
 */
public class FindCommand extends Command {
    public FindCommand(String args) {
        super("find", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) {
        String ans = joeDuck.getTasks().findTask(getArgs());
        return joeDuck.getUi().printResponse(ans);
    }
}
