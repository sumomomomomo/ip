package joeduck.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import joeduck.command.Command;
import joeduck.exception.InvalidCommandException;
public class ParserTest {
    @Test
    public void parseUserInput_validTodo_success() throws InvalidCommandException {
        String testInput = "todo asdf";
        Command res = new Parser().parseUserInput(testInput);
        assertEquals("todo", res.getCommand());
        assertEquals("asdf", res.getArgs());
    }

    @Test
    public void parseUserInput_emptyTodo_success() throws InvalidCommandException {
        String testInput = "todo";
        Command res = new Parser().parseUserInput(testInput);
        assertEquals("todo", res.getCommand());
        assertEquals("", res.getArgs());
    }

    @Test
    public void parseUserInput_numberInput_success() {
        String testInput = "123123";
        Command res = new Parser().parseUserInput(testInput);
        assertEquals("invalid", res.getCommand());
    }

    @Test
    public void parseUserInput_whitespaceInput_success() {
        String testInput = "    ";
        Command res = new Parser().parseUserInput(testInput);
        assertEquals("invalid", res.getCommand());
    }
}
