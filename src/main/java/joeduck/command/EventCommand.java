package joeduck.command;

import joeduck.JoeDuck;
import joeduck.exception.RegexMatchFailureException;
import joeduck.task.Event;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventCommand extends Command {
    public EventCommand(String args) {
        super("event", args);
    }

    @Override
    public String execute(JoeDuck joeDuck) throws RegexMatchFailureException, FileNotFoundException {
        Event e = getEvent(getArgs());
        joeDuck.getTasks().addTask(e);
        joeDuck.getStorage().writeList(joeDuck.getTasks().getTaskList());
        return joeDuck.getUi().printResponse("Added Event:\n" + e);
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
}