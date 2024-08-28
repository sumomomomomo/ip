package joeduck.task;

import java.util.List;

public class TaskList {
    private List<Task> tasks;

    public void setTaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> getTaskList() {
        return tasks;
    }

    public Task getTask(int index) {
        return tasks.get(index);
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(int index) {
        tasks.remove(index);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }
}
