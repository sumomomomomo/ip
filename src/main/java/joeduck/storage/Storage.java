package joeduck.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import joeduck.exception.InvalidTaskTypeException;
import joeduck.exception.RegexMatchFailureException;
import joeduck.exception.StorageLoadException;
import joeduck.task.Deadline;
import joeduck.task.Event;
import joeduck.task.Task;
import joeduck.task.Todo;
import joeduck.utils.Utils;

/**
 * Handles I/O to a file for persistence of the list of tasks.
 */
public class Storage {
    private static final String FOLDER_NAME = "ip_data";
    private static final String FILE_NAME = "tasks.txt";
    private static final String LOAD_REGEX_PATTERN = "^\\[(.)]\\[([ |X])] (.+)$";
    private final Path dataFolderPath;
    private final Path dataFilePath;
    private final Pattern loadRegexPattern;

    /**
     * Creates a new Storage instance. Singleton.
     */
    public Storage() {
        String homePath = System.getProperty("user.home");
        dataFolderPath = Paths.get(homePath, FOLDER_NAME);
        dataFilePath = Paths.get(homePath, FOLDER_NAME, FILE_NAME);
        loadRegexPattern = Pattern.compile(LOAD_REGEX_PATTERN);
    }

    /**
     * Parses a single line from tasks.txt, and returns the Task it represents.
     * Made public for easier testing.
     *
     * @param currLine String of the single line.
     * @return The Task it represents. Currently, they are: Todo, Deadline, Event
     * @throws InvalidTaskTypeException   Thrown when the line's task type is unrecognized.
     * @throws RegexMatchFailureException Thrown when regex fails when parsing Deadline or Event.
     */
    public Task getTaskFromLine(String currLine) throws InvalidTaskTypeException, RegexMatchFailureException {
        // TODO make some kind of class to hold regex patterns
        Matcher m = loadRegexPattern.matcher(currLine);
        if (!m.find()) {
            throw new RegexMatchFailureException("Error while parsing line: " + currLine);
        }

        String type = m.group(1);
        String done = m.group(2);
        boolean doneStatus = done.equals(Task.DONE_ICON);
        String descAndMisc = m.group(3);

        switch (type) {
        case "T": {
            Todo currTodo = new Todo(descAndMisc);
            currTodo.setDoneStatus(doneStatus);
            return currTodo;
        }
        case "D": {
            Pattern pd = Pattern.compile(Deadline.DESC_REGEX_PATTERN);
            return getDeadline(pd, descAndMisc, doneStatus);
        }
        case "E": {
            Pattern pe = Pattern.compile(Event.DESC_REGEX_PATTERN);
            return getEvent(pe, descAndMisc, doneStatus);
        }
        default: {
            throw new InvalidTaskTypeException("Unrecognized task type: " + type);
        }
        }
    }

    private static Deadline getDeadline(Pattern pd, String descAndMisc, boolean doneStatus)
            throws RegexMatchFailureException {
        Matcher md = pd.matcher(descAndMisc);
        if (!md.find()) {
            throw new RegexMatchFailureException("Error while parsing deadline: " + descAndMisc);
        }

        String desc = md.group(1);
        String date = md.group(2);
        LocalDate localDate = LocalDate.parse(date);
        String time = md.group(3);
        LocalTime localTime = LocalTime.parse(time);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

        Deadline currDeadline = new Deadline(desc, localDateTime);
        currDeadline.setDoneStatus(doneStatus);
        return currDeadline;
    }

    private static Event getEvent(Pattern pe, String descAndMisc, boolean doneStatus)
            throws RegexMatchFailureException {
        Matcher me = pe.matcher(descAndMisc);
        if (!me.find()) {
            throw new RegexMatchFailureException("Error while parsing event: " + descAndMisc);
        }

        String descE = me.group(1);
        String startDate = me.group(2);
        LocalDate d1 = LocalDate.parse(startDate);
        String startTime = me.group(3);
        LocalTime t1 = LocalTime.parse(startTime);
        String endDate = me.group(4);
        LocalDate d2 = LocalDate.parse(endDate);
        String endTime = me.group(5);
        LocalTime t2 = LocalTime.parse(endTime);

        LocalDateTime startDt = LocalDateTime.of(d1, t1);
        LocalDateTime endDt = LocalDateTime.of(d2, t2);

        Event currEvent = new Event(descE, startDt, endDt);
        currEvent.setDoneStatus(doneStatus);
        return currEvent;
    }

    /**
     * Reads tasks.txt, and returns a List of the tasks contained inside.
     * If tasks.txt does not exist, creates it and necessary directories.
     *
     * @return A List of Task.
     * @throws StorageLoadException Thrown when tasks.txt does not exist, and creation fails.
     */
    public List<Task> getTasksFromFile() throws StorageLoadException {
        List<Task> inputs = new ArrayList<>();
        if (!Files.exists(dataFolderPath)) {
            dataFolderPath.toFile().mkdirs();
        }

        if (!Files.exists(dataFilePath)) {
            try {
                dataFilePath.toFile().createNewFile();
            } catch (IOException e) {
                throw new StorageLoadException("Failed to create a file for saving/loading.");
            }
        }

        try {
            Scanner s = new Scanner(dataFilePath.toFile());
            while (s.hasNextLine()) {
                String currLine = s.nextLine().trim();
                try {
                    inputs.add(getTaskFromLine(currLine));
                } catch (InvalidTaskTypeException | RegexMatchFailureException e) {
                    System.out.println("Did not load " + e);
                }
            }
            s.close();
        } catch (FileNotFoundException e) {
            throw new StorageLoadException("File not found for scanner.");
        }
        return inputs;
    }

    /**
     * Overrides tasks.txt with a given List of Task.
     *
     * @param list List of Task to override tasks.txt with.
     * @throws FileNotFoundException Thrown when tasks.txt is not found.
     */
    public void writeList(List<Task> list) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(dataFilePath.toString())) {
            out.println(Utils.inputsToString(list, false));
        }
    }
}
