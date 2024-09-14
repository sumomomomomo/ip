package joeduck.command;

import java.io.FileNotFoundException;

import joeduck.JoeDuck;
import joeduck.exception.InvalidCommandException;
import joeduck.exception.RegexMatchFailureException;
/**
 * Command returned by Parser.
 */
public abstract class Command {
    protected final String command;
    protected final String args;

    /**
     * Create a new Command.
     * @param command Command's unique String identifier. Set by specific commands.
     * @param args All inputs after command.
     */
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

    /**
     * Called by JoeDuck.
     * @param joeDuck Main JoeDuck instance.
     * @return String representing result of the command.
     * @throws InvalidCommandException Thrown when the command is malformed.
     * @throws FileNotFoundException Thrown when file i/o fails.
     * @throws RegexMatchFailureException Thrown when command arguments are malformed.
     */
    public abstract String execute(JoeDuck joeDuck) throws InvalidCommandException,
            FileNotFoundException, RegexMatchFailureException;
}
