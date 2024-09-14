package joeduck.command;

import joeduck.JoeDuck;
import joeduck.exception.InvalidCommandException;
import joeduck.exception.RegexMatchFailureException;

import java.io.FileNotFoundException;

/**
 * Helper record returned by Parser.
 */
public abstract class Command {
    protected final String command;
    protected final String args;

    public Command(String command, String args) {
        this.command = command;
        this.args = args;
    }

    public String getCommand() {
        return command;
    }

    public String getArgs() {
        return args;
    }

    public abstract String execute(JoeDuck joeDuck) throws InvalidCommandException, FileNotFoundException, RegexMatchFailureException;
}
