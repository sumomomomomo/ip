package joeduck.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task with a single deadline.
 */
public class Deadline extends Task {
    public static final String DESC_REGEX_PATTERN = "^(.+) \\(by: (\\d{4}-\\d{2}-\\d{2}+) (\\d{2}:\\d{2})\\)$";
    private final LocalDateTime dueDate;

    /**
     * Creates a Deadline.
     */
    public Deadline(String description, LocalDateTime dueDate) {
        super(description);
        assert dueDate != null;
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + dueDate.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_PRINT)) + ")";
    }
    @Override
    public String toStringWrite() {
        return "[D]" + super.toString() + " (by: "
                + dueDate.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_WRITE)) + ")";
    }
}
