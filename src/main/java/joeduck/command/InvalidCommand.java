package joeduck.command;

import joeduck.JoeDuck;
import joeduck.exception.InvalidCommandException;

public class InvalidCommand extends Command {
    public InvalidCommand(String args) {
        super("invalid", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) throws InvalidCommandException {
        throw new InvalidCommandException("Invalid command!");
    }
}
