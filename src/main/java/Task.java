public abstract class Task {
    protected static final String DONE_ICON = "X";
    protected static final String DATE_TIME_PATTERN_PRINT = "MMM d yyyy HH:mm";
    protected static final String DATE_TIME_PATTERN_WRITE = "yyyy-MM-dd HH:mm";
    protected String description;
    protected boolean isDone;

    protected Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public void setDoneStatus(boolean s) {
        this.isDone = s;
    }

    public String getStatusIcon() {
        return (isDone ? DONE_ICON : " ");
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    public abstract String toStringWrite();
}
