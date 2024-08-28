public class Event extends Task {
    private final String startDate;
    private final String endDate;
    protected static final String REGEX_PATTERN = "^(.+) \\(from: (.+) to: (.+)\\)$";
    public Event(String description, String startDate, String endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + startDate + " to: " + endDate + ")";
    }
}
