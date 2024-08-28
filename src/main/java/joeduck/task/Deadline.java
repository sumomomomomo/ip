package joeduck.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Deadline extends Task {
    private final LocalDateTime dueDate;

    public static final String DESC_REGEX_PATTERN = "^(.+) \\(by: (\\d{4}-\\d{2}-\\d{2}+) (\\d{2}:\\d{2})\\)$";

    public Deadline(String description, LocalDateTime dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " +
                dueDate.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_PRINT)) + ")";
    }

    public String toStringWrite() {
        return "[D]" + super.toString() + " (by: " +
                dueDate.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_WRITE)) + ")";
    }
}
