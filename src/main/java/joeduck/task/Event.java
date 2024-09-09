package joeduck.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that takes place within a certain period of time.
 */
public class Event extends Task {
    public static final String DESC_REGEX_PATTERN = "(.+) \\(from: (\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2}) "
            + "to: (\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2})\\)";
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    /**
     * Creates an Event.
     */
    public Event(String description, LocalDateTime startDate, LocalDateTime endDate) {
        super(description);
        assert startDate != null;
        assert endDate != null;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + startDate.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_PRINT)) + " to: "
                + endDate.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_PRINT)) + ")";
    }

    /**
     * Returns the string representation of an Event.
     * For writing to tasks.txt.
     * @return String representation of the Event, for printing.
     */
    public String toStringWrite() {
        return "[E]" + super.toString() + " (from: "
                + startDate.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_WRITE)) + " to: "
                + endDate.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_WRITE)) + ")";
    }
}
