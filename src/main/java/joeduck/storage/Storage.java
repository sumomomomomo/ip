package joeduck.storage;

import joeduck.exception.InvalidTaskTypeException;
import joeduck.exception.RegexMatchFailureException;
import joeduck.exception.StorageLoadException;
import joeduck.task.Deadline;
import joeduck.task.Event;
import joeduck.task.Task;
import joeduck.task.Todo;
import joeduck.utils.Utils;

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

public class Storage {
    private static final String FILE_NAME = "ip_data";
    private static final String LOAD_REGEX_PATTERN = "^\\[(.)]\\[([ |X])] (.+)$";
    private final Path dataFolderPath;
    private final Path dataFilePath;
    private final Pattern loadRegexPattern;

    public Storage() {
        String homePath = System.getProperty("user.home");
        dataFolderPath = Paths.get(homePath, FILE_NAME);
        dataFilePath = Paths.get(homePath, "ip_data", "tasks.txt");
        loadRegexPattern = Pattern.compile(LOAD_REGEX_PATTERN);
    }

    // made public for unit testing
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
            case "T":
                Todo currTodo = new Todo(descAndMisc);
                currTodo.setDoneStatus(doneStatus);
                return currTodo;
            case "D":
                Pattern pd = Pattern.compile(Deadline.DESC_REGEX_PATTERN);
                return getDeadline(pd, descAndMisc, doneStatus);
            case "E":
                Pattern pe = Pattern.compile(Event.DESC_REGEX_PATTERN);
                return getEvent(pe, descAndMisc, doneStatus);
        }
        throw new InvalidTaskTypeException("Unrecognized task type: " + type);
    }

    private static Deadline getDeadline(Pattern pd, String descAndMisc, boolean doneStatus) throws RegexMatchFailureException {
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

    private static Event getEvent(Pattern pe, String descAndMisc, boolean doneStatus) throws RegexMatchFailureException {
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

    public List<Task> getTasksFromFile() throws StorageLoadException {
        List<Task> inputs = new ArrayList<>();
        if (!Files.exists(dataFolderPath)) {
            dataFolderPath.toFile().mkdirs();
        }

        if (Files.exists(dataFilePath)) {
            try {
                Scanner s = new Scanner(dataFilePath.toFile());
                while (s.hasNextLine()) {
                    String currLine = s.nextLine().trim();
                    inputs.add(getTaskFromLine(currLine));
                }
                s.close();
            } catch (FileNotFoundException | InvalidTaskTypeException |
                     RegexMatchFailureException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                if (!dataFilePath.toFile().createNewFile()) {
                    throw new IOException();
                }
            } catch (IOException e) {
                throw new StorageLoadException("Failed to create a file for saving/loading.");
            }
        }

        return inputs;
    }

    public void writeList(List<Task> list) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(dataFilePath.toString())) {
            out.println(Utils.inputsToString(list, false));
        }
    }
}
