14/9/2024: Used AI to refactor JoeDuck's huge code block, and separate it into
multiple separate Command classes.

Used https://huggingface.co/failspy/llama-3-70B-Instruct-abliterated in Q4 GGUF quant, 
run locally on 2x Tesla P40 with llama.cpp backend and Ollama frontend.

### USER
Given the following code, refactor executeCommand so that multiple command types 
inherit from a base Command, and the switch case statement is shifted to Parser.java:

JoeDuck.java
package joeduck;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import joeduck.command.Command;
import joeduck.exception.InvalidCommandException;
import joeduck.exception.JoeDuckException;
import joeduck.exception.RegexMatchFailureException;
import joeduck.exception.StorageLoadException;
import joeduck.parser.Parser;
import joeduck.storage.Storage;
import joeduck.task.Deadline;
import joeduck.task.Event;
import joeduck.task.Task;
import joeduck.task.TaskList;
import joeduck.task.Todo;
import joeduck.ui.MainWindow;
import joeduck.ui.Ui;
import joeduck.utils.Utils;

/**
* Main class. Interactive chatbot.
  */
  public class JoeDuck extends Application {
  private static final List<String> SUPPORTED_MASS_COMMANDS = List.of("mark", "unmark", "remove", "delete");
  private final Ui ui;
  private final Storage storage;
  private final TaskList tasks;
  private final Parser parser;

  /**
    * Constructor for singleton JoeDuck. Represents the chatbot instance.
      */
      public JoeDuck() {
      ui = new Ui();
      storage = new Storage();
      parser = new Parser();
      try {
      tasks = new TaskList();
      tasks.setTaskList(storage.getTasksFromFile());
      } catch (StorageLoadException e) {
      // cry
      throw new RuntimeException(e);
      }
      }

  private String executeCommand(Command currCommand) {
  try {
  switch (currCommand.command()) {
  case "bye", "exit": {
  Platform.exit();
  return ui.onExit();
  }
  case "list": {
  return ui.printResponse(Utils.inputsToString(tasks.getTaskList(), true));
  }
  case "mark": {
  String targetIndexStr = currCommand.args();
  int targetIndex = Integer.parseInt(targetIndexStr) - 1;
  Task targetTask = tasks.getTask(targetIndex);
  targetTask.setDoneStatus(true);
  storage.writeList(tasks.getTaskList());
  return ui.printResponse("Marked " + targetTask);
  }
  case "unmark": {
  String targetIndexStr = currCommand.args();
  int targetIndex = Integer.parseInt(targetIndexStr) - 1;
  Task targetTask = tasks.getTask(targetIndex);
  targetTask.setDoneStatus(false);
  storage.writeList(tasks.getTaskList());
  return ui.printResponse("Unmarked " + targetTask);
  }
  case "delete", "remove": {
  String targetIndexStr = currCommand.args();
  int targetIndex = Integer.parseInt(targetIndexStr) - 1;
  Task targetTask = tasks.getTask(targetIndex);
  tasks.removeTask(targetIndex);
  storage.writeList(tasks.getTaskList());
  return ui.printResponse("Removed " + targetTask);
  }
  case "find": {
  String keyword = currCommand.args();
  return ui.printResponse(tasks.findTask(keyword));
  }
  case "todo": {
  String todoString = currCommand.args();
  if (todoString.isEmpty()) {
  throw new InvalidCommandException("Todo requires a description.");
  }
  Todo t = new Todo(todoString);
  tasks.addTask(t);
  storage.writeList(tasks.getTaskList());
  return ui.printResponse("Added Todo:\n" + t);
  }
  case "deadline": {
  String deadlineString = currCommand.args();
  Deadline d = getDeadline(deadlineString);
  tasks.addTask(d);
  storage.writeList(tasks.getTaskList());
  return ui.printResponse("Added Deadline:\n" + d);
  }
  case "event": {
  String deadlineString = currCommand.args();
  Event e = getEvent(deadlineString);
  tasks.addTask(e);
  storage.writeList(tasks.getTaskList());
  return ui.printResponse("Added Event:\n" + e);
  }
  case "mass": {
  String massCommand = currCommand.args();
  Command newCommand = parser.parseUserInput(massCommand);
  return executeMassCommand(newCommand);
  }
  default: {
  throw new InvalidCommandException("Invalid command!");
  }
  }
  } catch (JoeDuckException | NumberFormatException | IndexOutOfBoundsException e) {
  // TODO specially handle NumberFormatException and IndexOutOfBoundsException
  return ui.printError(e.getMessage());
  } catch (FileNotFoundException e) {
  throw new RuntimeException(e);
  }
  }

  private static Event getEvent(String deadlineString) throws RegexMatchFailureException {
  String pattern = "(.+) /from (\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2}) "
  + "/to (\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2})";
  Pattern pe = Pattern.compile(pattern);
  Matcher me = pe.matcher(deadlineString);
  if (!me.find()) {
  throw new RegexMatchFailureException("Arguments for creating event is incorrect");
  }

       String desc = me.group(1);
       String startDate = me.group(2);
       LocalDate d1 = LocalDate.parse(startDate);
       String startTime = me.group(3);
       LocalTime t1 = LocalTime.parse(startTime);
       String endDate = me.group(4);
       LocalDate d2 = LocalDate.parse(endDate);
       String endTime = me.group(5);
       LocalTime t2 = LocalTime.parse(endTime);

       LocalDateTime startDt = LocalDateTime.of(d1, t1);
       LocalDateTime endDt = LocalDateTime.of(d2, t2);
       return new Event(desc, startDt, endDt);
  }

  private static Deadline getDeadline(String deadlineString) throws RegexMatchFailureException {
  String pattern = "(.+) /by (\\d{4}-\\d{2}-\\d{2}+) (\\d{2}:\\d{2})";
  Pattern pd = Pattern.compile(pattern);
  Matcher md = pd.matcher(deadlineString);
  if (!md.find()) {
  throw new RegexMatchFailureException("Arguments for creating deadline is incorrect");
  }

       String desc = md.group(1);
       String date = md.group(2);
       LocalDate localDate = LocalDate.parse(date);

       String time = md.group(3);
       LocalTime localTime = LocalTime.parse(time);
       LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

       return new Deadline(desc, localDateTime);
  }

  @Override
  public void start(Stage stage) {
  try {
  FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/MainWindow.fxml"));
  AnchorPane ap = fxmlLoader.load();
  Scene scene = new Scene(ap);
  stage.setScene(scene);
  fxmlLoader.<MainWindow>getController().setJoeDuck(this);
  stage.show();
  } catch (IOException e) {
  e.printStackTrace();
  }
  }

  public String getResponse(String input) {
  try {
  Command currCommand = parser.parseUserInput(input);
  return executeCommand(currCommand);
  } catch (InvalidCommandException e) {
  throw new RuntimeException(e);
  }
  }
  private String executeMassCommand(Command massCommand) {
  if (!SUPPORTED_MASS_COMMANDS.contains(massCommand.command())) {
  return ui.printError("Unsupported mass command: " + massCommand.command());
  }
  StringBuilder ans = new StringBuilder();
  // special behaviour for remove
  // TODO add checking that every number is valid
  if (Objects.equals(massCommand.command(), "remove")
  || Objects.equals(massCommand.command(), "delete")) {
  List<Task> taskListCopy = tasks.getTaskListCopy();
  for (String targetIndexStr : massCommand.args().split("\\s+")) {
  int targetIndex = Integer.parseInt(targetIndexStr) - 1;
  Task targetTask = taskListCopy.get(targetIndex);
  tasks.removeTask(targetTask);
  ans.append("Removed ").append(targetTask).append("\n");
  }
  return ans.toString().trim();
  }

       // behaviour for mark, unmark
       for (String targetIndexStr : massCommand.args().split("\\s+")) {
           ans.append(executeCommand(new Command(massCommand.command(), targetIndexStr))).append("\n");
       }
       return ans.toString().trim();
  }
  }

Parser.java
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

Command.java
package joeduck.command;

/**
* Helper record returned by Parser.
  */
  public record Command(String command, String args) {
  }


### ASSISTANT
Here is the refactored code:

**Command.java**
```java
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

    public abstract String execute(JoeDuck joeDuck);
}
```

** ByeCommand.java**
```java
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
```

**ListCommand.java**
```java
public class ListCommand extends Command {
    public ListCommand(String args) {
        super("list", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) {
        return joeDuck.getUi().printResponse(Utils.inputsToString(joeDuck.getTasks().getTaskList(), true));
    }
}
```

**MarkCommand.java**
```java
public class MarkCommand extends Command {
    public MarkCommand(String args) {
        super("mark", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) {
        int targetIndex = Integer.parseInt(getArgs()) - 1;
        Task targetTask = joeDuck.getTasks().getTask(targetIndex);
        targetTask.setDoneStatus(true);
        joeDuck.getStorage().writeList(joeDuck.getTasks().getTaskList());
        return joeDuck.getUi().printResponse("Marked " + targetTask);
    }
}
```

**UnmarkCommand.java**
```java
public class UnmarkCommand extends Command {
    public UnmarkCommand(String args) {
        super("unmark", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) {
        int targetIndex = Integer.parseInt(getArgs()) - 1;
        Task targetTask = joeDuck.getTasks().getTask(targetIndex);
        targetTask.setDoneStatus(false);
        joeDuck.getStorage().writeList(joeDuck.getTasks().getTaskList());
        return joeDuck.getUi().printResponse("Unmarked " + targetTask);
    }
}
```

**DeleteCommand.java**
```java
public class DeleteCommand extends Command {
    public DeleteCommand(String args) {
        super("delete", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) {
        int targetIndex = Integer.parseInt(getArgs()) - 1;
        Task targetTask = joeDuck.getTasks().getTask(targetIndex);
        joeDuck.getTasks().removeTask(targetIndex);
        joeDuck.getStorage().writeList(joeDuck.getTasks().getTaskList());
        return joeDuck.getUi().printResponse("Removed " + targetTask);
    }
}
```

**FindCommand.java**
```java
public class FindCommand extends Command {
    public FindCommand(String args) {
        super("find", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) {
        return joeDuck.getUi().printResponse(joeDuck.getTasks().findTask(getArgs()));
    }
}
```

**TodoCommand.java**
```java
public class TodoCommand extends Command {
    public TodoCommand(String args) {
        super("todo", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) {
        if (getArgs().isEmpty()) {
            throw new InvalidCommandException("Todo requires a description.");
        }
        Todo t = new Todo(getArgs());
        joeDuck.getTasks().addTask(t);
        joeDuck.getStorage().writeList(joeDuck.getTasks().getTaskList());
        return joeDuck.getUi().printResponse("Added Todo:\n" + t);
    }
}
```

**DeadlineCommand.java**
```java
public class DeadlineCommand extends Command {
    public DeadlineCommand(String args) {
        super("deadline", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) {
        Deadline d = joeDuck.getDeadline(getArgs());
        joeDuck.getTasks().addTask(d);
        joeDuck.getStorage().writeList(joeDuck.getTasks().getTaskList());
        return joeDuck.getUi().printResponse("Added Deadline:\n" + d);
    }
}
```

**EventCommand.java**
```java
public class EventCommand extends Command {
    public EventCommand(String args) {
        super("event", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) {
        Event e = joeDuck.getEvent(getArgs());
        joeDuck.getTasks().addTask(e);
        joeDuck.getStorage().writeList(joeDuck.getTasks().getTaskList());
        return joeDuck.getUi().printResponse("Added Event:\n" + e);
    }
}
```

**MassCommand.java**
```java
public class MassCommand extends Command {
    public MassCommand(String args) {
        super("mass", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) {
        Command newCommand = joeDuck.getParser().parseUserInput(getArgs());
        return joeDuck.executeCommand(newCommand);
    }
}
```

**Parser.java**
```java
public class Parser {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("([a-zA-Z]+)");

    public Command parseUserInput(String input) throws InvalidCommandException {
        String currInput = input.trim();
        Matcher mm = COMMAND_PATTERN.matcher(currInput);
        if (mm.find()) {
            String currCommand = mm.group(1);
            String args = "";
            if (currCommand.length() < currInput.length()) {
                args = currInput.substring(currCommand.length() + 1);
            }

            switch (currCommand) {
                case "bye":
                    return new ByeCommand(args);
                case "list":
                    return new ListCommand(args);
                case "mark":
                    return new MarkCommand(args);
                case "unmark":
                    return new UnmarkCommand(args);
                case "delete":
                    return new DeleteCommand(args);
                case "find":
                    return new FindCommand(args);
                case "todo":
                    return new TodoCommand(args);
                case "deadline":
                    return new DeadlineCommand(args);
                case "event":
                    return new EventCommand(args);
                case "mass":
                    return new MassCommand(args);
                default:
                    throw new InvalidCommandException("Invalid command!");
            }
        } else {
            throw new InvalidCommandException("Invalid command!");
        }
    }
}
```

**JoeDuck.java**
```java
public class JoeDuck extends Application {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private final Parser parser;

    public JoeDuck() {
        ui = new Ui();
        storage = new Storage();
        parser = new Parser();
        try {
            tasks = new TaskList();
            tasks.setTaskList(storage.getTasksFromFile());
        } catch (StorageLoadException e) {
            // cry
            throw new RuntimeException(e);
        }
    }

    public String executeCommand(Command currCommand) {
        return currCommand.execute(this);
    }

    // ...
}
```

I hope this helps! Let me know if you have any questions.

