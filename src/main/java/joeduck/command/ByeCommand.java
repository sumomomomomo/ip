package joeduck.command;

import javafx.application.Platform;
import joeduck.JoeDuck;

public class ByeCommand extends Command {
    public ByeCommand(String args) {
        super("bye", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) {
        Platform.exit();
        return joeDuck.getUi().onExit();
    }
}