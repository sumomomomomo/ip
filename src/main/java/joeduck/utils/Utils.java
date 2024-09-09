package joeduck.utils;

import java.util.List;

import joeduck.task.Task;

/**
 * Helper functions not specific to any other class.
 */
public class Utils {

    /**
     * Gets the string representation of a List of Task.
     * If forPrinting is true, will get the equivalent representation meant for writing to tasks.txt.
     * Else, will get the representation meant for printing to the user.
     * @param list List of Task to be converted to String.
     * @param forPrinting Boolean representing if the String is for showing to the user.
     * @return String representation of the List of Task.
     */
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
