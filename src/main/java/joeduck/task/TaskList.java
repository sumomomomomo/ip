package joeduck.task;

import java.util.List;

/**
 * Handles adding/removing of tasks to an internal List of Task.
 * @author Matthew Ho
 */
public class TaskList {
    private List<Task> tasks;

    /**
     * Overrides the current List of Task.
     * @param tasks New List of Task.
     */
    public void setTaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Gets the current List of Task.
     * @return Current List of Task.
     */
    public List<Task> getTaskList() {
        return tasks;
    }

    /**
     * Gets a certain Task by index.
     * @param index Index of the Task.
     * @return The Task.
     */
    public Task getTask(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a Task.
     * @param task The Task to be added.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Removes a Task by index.
     * @param index Index of the Task to be removed.
     */
    public void removeTask(int index) {
        tasks.remove(index);
    }

    /**
     * Removes a Task by the Task itself.
     * @param task Task to be removed.
     */
    public void removeTask(Task task) {
        tasks.remove(task);
    }
}
