package joeduck.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import joeduck.command.ByeCommand;
import joeduck.command.Command;
import joeduck.command.DeadlineCommand;
import joeduck.command.DeleteCommand;
import joeduck.command.EventCommand;
import joeduck.command.FindCommand;
import joeduck.command.InvalidCommand;
import joeduck.command.ListCommand;
import joeduck.command.MarkCommand;
import joeduck.command.MassCommand;
import joeduck.command.TodoCommand;
import joeduck.command.UnmarkCommand;

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
     */
    public Command parseUserInput(String input) {
        String currInput = input.trim();
        Matcher mm = COMMAND_PATTERN.matcher(currInput);
        if (!mm.find()) {
            return new InvalidCommand("Invalid command!");
        }
        String currCommand = mm.group(1);
        String args = "";
        if (currCommand.length() < currInput.length()) {
            args = currInput.substring(currCommand.length() + 1);
        }
        return switch (currCommand) {
        case "bye" -> new ByeCommand(args);
        case "list" -> new ListCommand(args);
        case "mark" -> new MarkCommand(args);
        case "unmark" -> new UnmarkCommand(args);
        case "delete", "remove" -> new DeleteCommand(args);
        case "find" -> new FindCommand(args);
        case "todo" -> new TodoCommand(args);
        case "deadline" -> new DeadlineCommand(args);
        case "event" -> new EventCommand(args);
        case "mass" -> new MassCommand(args);
        default -> new InvalidCommand(args);
        };
    }
}

