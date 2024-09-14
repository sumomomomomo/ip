package joeduck.task;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles adding/removing of tasks to an internal List of Task.
 * @author Matthew Ho
 */
public class TaskList {
    private List<Task> tasks;

    /**
     * Overrides the current List of Task.
     *
     * @param tasks New List of Task.
     */
    public void setTaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Gets the current List of Task.
     *
     * @return Current List of Task.
     */
    public List<Task> getTaskList() {
        return tasks;
    }

    /**
     * Makes a copy of the current List of Task.
     *
     * @return Copy of current List of Task.
     */
    public List<Task> getTaskListCopy() {
        return new ArrayList<>(tasks);
    }

    /**
     * Gets a certain Task by index.
     *
     * @param index Index of the Task.
     * @return The Task.
     */
    public Task getTask(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a Task.
     *
     * @param task The Task to be added.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Removes a Task by index.
     *
     * @param index Index of the Task to be removed.
     */
    public void removeTask(int index) {
        tasks.remove(index);
    }

    /**
     * Removes a Task by the Task itself.
     *
     * @param task Task to be removed.
     */
    public void removeTask(Task task) {
        tasks.remove(task);
    }

    /**
     * Returns a String of all tasks that match a given String keyword.
     * Uses regex to match every instance of keyword.
     * TODO: clean up keyword with special characters?
     *
     * @param keyword Keyword to search.
     * @return String of each task, prepended with index, separated by newline.
     */
    public String findTask(String keyword) {
        Pattern p = Pattern.compile("(" + keyword + ")");
        StringBuilder ans = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            Task currTask = tasks.get(i);
            Matcher m = p.matcher(currTask.toString());
            if (m.find()) {
                ans.append(i + 1).append(". ").append(currTask).append("\n");
            }
        }
        return ans.toString().trim();
    }
}
