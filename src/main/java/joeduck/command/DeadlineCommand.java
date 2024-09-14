package joeduck.command;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import joeduck.JoeDuck;
import joeduck.exception.RegexMatchFailureException;
import joeduck.task.Deadline;

/**
 * Creates a new deadline.
 */
public class DeadlineCommand extends Command {
    public DeadlineCommand(String args) {
        super("deadline", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) throws RegexMatchFailureException, FileNotFoundException {
        Deadline d = getDeadline(getArgs());
        joeDuck.getTasks().addTask(d);
        joeDuck.getStorage().writeList(joeDuck.getTasks().getTaskList());
        return joeDuck.getUi().printResponse("Added Deadline:\n" + d);
    }

    private static Deadline getDeadline(String deadlineString) throws RegexMatchFailureException {
        // Get deadline details
        String pattern = "(.+) /by (\\d{4}-\\d{2}-\\d{2}+) (\\d{2}:\\d{2})";
        Pattern pd = Pattern.compile(pattern);
        Matcher md = pd.matcher(deadlineString);
        if (!md.find()) {
            throw new RegexMatchFailureException("Arguments for creating deadline is incorrect");
        }

        // Extract matched groups
        String desc = md.group(1);
        String date = md.group(2);
        LocalDate localDate = LocalDate.parse(date);
        String time = md.group(3);
        LocalTime localTime = LocalTime.parse(time);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

        return new Deadline(desc, localDateTime);
    }
}
