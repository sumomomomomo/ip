import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event extends Task {
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    protected static final String DESC_REGEX_PATTERN = "(.+) \\(from: (\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2}) " +
            "to: (\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2})\\)";
    public Event(String description, LocalDateTime startDate, LocalDateTime endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " +
                startDate.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_PRINT)) + " to: " +
                endDate.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_PRINT)) + ")";
    }

    public String toStringWrite() {
        return "[E]" + super.toString() + " (from: " +
                startDate.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_WRITE)) + " to: " +
                endDate.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_WRITE)) + ")";
    }
}
