public class Deadline extends Task {
    private final String dueDate;

    protected static final String REGEX_PATTERN = "^(.+) \\(by: (.+)\\)$";
    public Deadline(String description, String dueDate) {
        super(description);
        this.dueDate = dueDate;
    }
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + dueDate + ")";
    }
}
