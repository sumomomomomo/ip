package joeduck.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import joeduck.command.Command;
import joeduck.exception.InvalidCommandException;

/**
 * Handles raw input from the user.
 * Transforms raw input into a Command.
 */
public class Parser {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("([a-zA-Z]+)");

    /**
     * Parses a single line of raw user input.
     * @param input String of the user's raw input.
     * @return Command, representing the user's command and arguments.
     * @throws InvalidCommandException Thrown when a non-alphabetical sequence is the first sequence.
     */
    public Command parseUserInput(String input) throws InvalidCommandException {
        String currInput = input.trim();
        String currCommand = "";
        Matcher mm = COMMAND_PATTERN.matcher(currInput);
        if (mm.find()) {
            currCommand = mm.group(1);
            String args = "";
            if (currCommand.length() < currInput.length()) {
                args = currInput.substring(currCommand.length() + 1);
            }
            return new Command(currCommand, args);
        } else {
            throw new InvalidCommandException("Invalid command!");
        }
    }
}
