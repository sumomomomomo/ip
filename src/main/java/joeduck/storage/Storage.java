package joeduck.storage;

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

    public Storage() {
        String homePath = System.getProperty("user.home");
        dataFolderPath = Paths.get(homePath, FILE_NAME);
        dataFilePath = Paths.get(homePath, "ip_data", "tasks.txt");
    }
    public List<Task> getTasksFromFile() throws StorageLoadException {
        List<Task> inputs = new ArrayList<>();
        // try and find saved data
        // TODO refactor regex so compile isn't spammed everywhere
        // TODO add error handling for invalid save files
        if (!Files.exists(dataFolderPath)) {
            dataFolderPath.toFile().mkdirs();
        }

        if (Files.exists(dataFilePath)) {
            //printResponse("tasks.json found!");
            // load saved data
            try {
                Scanner s = new Scanner(dataFilePath.toFile());
                Pattern p = Pattern.compile(LOAD_REGEX_PATTERN);
                while (s.hasNextLine()) {
                    String currLine = s.nextLine().trim();
                    Matcher m = p.matcher(currLine);
                    m.find();

                    String type = m.group(1);
                    String done = m.group(2);
                    boolean doneStatus = done.equals(Task.DONE_ICON);
                    String descAndMisc = m.group(3);

                    switch (type) {
                        case "T":
                            Todo currTodo = new Todo(descAndMisc);
                            currTodo.setDoneStatus(doneStatus);
                            inputs.add(currTodo);
                            break;
                        case "D":
                            Pattern pd = Pattern.compile(Deadline.DESC_REGEX_PATTERN);
                            Matcher md = pd.matcher(descAndMisc);
                            md.find();

                            String desc = md.group(1);
                            String date = md.group(2);
                            LocalDate localDate = LocalDate.parse(date);
                            String time = md.group(3);
                            LocalTime localTime = LocalTime.parse(time);
                            LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

                            Deadline currDeadline = new Deadline(desc, localDateTime);
                            currDeadline.setDoneStatus(doneStatus);
                            inputs.add(currDeadline);
                            break;
                        case "E":
                            Pattern pe = Pattern.compile(Event.DESC_REGEX_PATTERN);
                            Matcher me = pe.matcher(descAndMisc);
                            me.find();

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
                            inputs.add(currEvent);
                            break;
                    }

                }
                s.close();
                //printResponse("Tasks loaded successfully.");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                dataFilePath.toFile().createNewFile();
                //printResponse("File " + dataFilePath + " created.");
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
