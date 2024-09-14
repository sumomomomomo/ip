package joeduck.command;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;

import joeduck.JoeDuck;
import joeduck.exception.InvalidCommandException;
import joeduck.task.Task;

/**
 * Command for batch operations.
 */
public class MassCommand extends Command {
    private static final List<String> SUPPORTED_MASS_COMMANDS = List.of("mark", "unmark", "remove", "delete");

    public MassCommand(String args) {
        super("mass", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) throws InvalidCommandException, FileNotFoundException {
        // First word of args of mass command is the new command
        Command massCommand = joeDuck.getParser().parseUserInput(getArgs());
        return executeMassCommand(joeDuck, massCommand);
    }

    private String executeMassCommand(JoeDuck joeDuck, Command massCommand) throws InvalidCommandException,
            FileNotFoundException, IndexOutOfBoundsException {
        if (!SUPPORTED_MASS_COMMANDS.contains(massCommand.getCommand())) {
            throw new InvalidCommandException("Unsupported mass command: " + massCommand.getCommand());
        }
        StringBuilder ans = new StringBuilder();
        // special behaviour for remove, so that batch remove is done by task itself instead of index
        // TODO add checking that every number is valid
        if (Objects.equals(massCommand.getCommand(), "remove")
                || Objects.equals(massCommand.getCommand(), "delete")) {
            List<Task> taskListCopy = joeDuck.getTasks().getTaskListCopy();
            for (String targetIndexStr : massCommand.getArgs().split("\\s+")) {
                int targetIndex = Integer.parseInt(targetIndexStr) - 1;
                Task targetTask = taskListCopy.get(targetIndex);
                joeDuck.getTasks().removeTask(targetTask);
                ans.append("Removed ").append(targetTask).append("\n");
            }
            return ans.toString().trim();
        }

        // behaviour for mark, unmark
        for (String targetIndexStr : massCommand.getArgs().split("\\s+")) {
            switch (massCommand.getCommand()) {
            case "mark": {
                ans.append(new MarkCommand(targetIndexStr).execute(joeDuck));
                break;
            }
            case "unmark": {
                ans.append(new UnmarkCommand(targetIndexStr).execute(joeDuck));
                break;
            }
            default: {
                throw new RuntimeException("Default in MassCommand");
            }
            }
            ans.append("\n");
        }

        return ans.toString().trim();
    }
}
