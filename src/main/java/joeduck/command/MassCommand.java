package joeduck.command;

import joeduck.JoeDuck;
import joeduck.exception.InvalidCommandException;
import joeduck.task.Task;

import java.util.List;
import java.util.Objects;

public class MassCommand extends Command {
    private static final List<String> SUPPORTED_MASS_COMMANDS = List.of("mark", "unmark", "remove", "delete");

    public MassCommand(String args) {
        super("mass", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) throws InvalidCommandException {
        Command newCommand = joeDuck.getParser().parseUserInput(getArgs());
        return executeMassCommand(newCommand);
    }

    private String executeMassCommand(Command massCommand) {
        return massCommand.getCommand();
//        if (!SUPPORTED_MASS_COMMANDS.contains(massCommand.command())) {
//            return ui.printError("Unsupported mass command: " + massCommand.command());
//        }
//        StringBuilder ans = new StringBuilder();
//        // special behaviour for remove
//        // TODO add checking that every number is valid
//        if (Objects.equals(massCommand.command(), "remove")
//                || Objects.equals(massCommand.command(), "delete")) {
//            List<Task> taskListCopy = tasks.getTaskListCopy();
//            for (String targetIndexStr : massCommand.args().split("\\s+")) {
//                int targetIndex = Integer.parseInt(targetIndexStr) - 1;
//                Task targetTask = taskListCopy.get(targetIndex);
//                tasks.removeTask(targetTask);
//                ans.append("Removed ").append(targetTask).append("\n");
//            }
//            return ans.toString().trim();
//        }
//
//        // behaviour for mark, unmark
//        for (String targetIndexStr : massCommand.args().split("\\s+")) {
//            ans.append(executeCommand(new Command(massCommand.command(), targetIndexStr))).append("\n");
//        }
//        return ans.toString().trim();
    }
}