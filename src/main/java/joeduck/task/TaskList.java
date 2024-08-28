package joeduck.task;

import joeduck.task.Task;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public void removeTask(Task task) {
        tasks.remove(task);
    }
}
