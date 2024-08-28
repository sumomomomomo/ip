package joeduck.parser;

import joeduck.command.Command;
import joeduck.exception.InvalidCommandException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private final static Pattern COMMAND_PATTERN = Pattern.compile("([a-zA-Z]+)");
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
            throw new InvalidCommandException("Could not parse!");
        }
    }
}
