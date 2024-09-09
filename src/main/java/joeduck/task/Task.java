package joeduck.task;

/**
 * Represents any task to be done by the user.
 */
public abstract class Task {
    public static final String DONE_ICON = "X";
    public static final String DATE_TIME_PATTERN_PRINT = "MMM d yyyy HH:mm";
    public static final String DATE_TIME_PATTERN_WRITE = "yyyy-MM-dd HH:mm";
    protected String description;
    protected boolean isDone;

    protected Task(String description) {
        assert !description.isEmpty();
        this.description = description;
        this.isDone = false;
    }

    /**
     * Sets the internal done boolean.
     * @param s Doneness.
     */
    public void setDoneStatus(boolean s) {
        this.isDone = s;
    }

    /**
     * Gets the string representing doneness if done, or a single space if not.
     * @return String representing doneness.
     */
    public String getStatusIcon() {
        return (isDone ? DONE_ICON : " ");
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Used for writing to tasks.txt.
     * @return String representation of the Task.
     */
    public abstract String toStringWrite();
}
