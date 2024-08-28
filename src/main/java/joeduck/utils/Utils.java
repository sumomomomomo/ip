package joeduck.utils;

import joeduck.task.Task;

import java.util.List;

public class Utils {
    public static String inputsToString(List<Task> list, boolean forPrinting) {
        StringBuilder ans = new StringBuilder();
        int count = 1;
        for (Task s : list) {
            if (forPrinting) {
                ans.append(count).append(". ").append(s.toString());
            } else {
                ans.append(s.toStringWrite());
            }
            if (count < list.size()) {
                ans.append("\n");
            }
            count++;
        }
        return ans.toString();
    }
}
