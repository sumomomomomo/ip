package joeduck.command;

import joeduck.JoeDuck;

public class FindCommand extends Command {
    public FindCommand(String args) {
        super("find", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) {
        return joeDuck.getUi().printResponse(joeDuck.getTasks().findTask(getArgs()));
    }
}