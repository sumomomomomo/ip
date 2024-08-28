package joeduck.task;

public abstract class Task {
    public static final String DONE_ICON = "X";
    public static final String DATE_TIME_PATTERN_PRINT = "MMM d yyyy HH:mm";
    public static final String DATE_TIME_PATTERN_WRITE = "yyyy-MM-dd HH:mm";
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
