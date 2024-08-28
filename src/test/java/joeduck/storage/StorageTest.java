package joeduck.storage;

import joeduck.task.Task;

import joeduck.exception.InvalidTaskTypeException;
import joeduck.exception.RegexMatchFailureException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class StorageTest {
    @Test
    public void getTaskFromLine_validTodo_success() throws RegexMatchFailureException, InvalidTaskTypeException {
        String testInput = "[T][X] hi";
        Task res = new Storage().getTaskFromLine(testInput);
        assertEquals("[T][X] hi", res.toString());
        assertEquals("X", res.getStatusIcon());
    }

    @Test
    public void getTaskFromLine_emptyTodo_throwsException() {
        String testInput = "[T][X]";
        try {
            Task res = new Storage().getTaskFromLine(testInput);
            fail();
        } catch (Exception e) {
            assertEquals("Error while parsing line: [T][X]", e.getMessage());
        }
    }

    @Test
    public void getTaskFromLine_validDeadline_success() throws RegexMatchFailureException, InvalidTaskTypeException {
        String testInput = "[D][X] deasdfasdf (by: 2024-01-01 10:10)";
        Task res = new Storage().getTaskFromLine(testInput);
        assertEquals("[D][X] deasdfasdf (by: Jan 1 2024 10:10)", res.toString());
        assertEquals("X", res.getStatusIcon());
    }

    @Test
    public void getTaskFromLine_deadlineDateOverLimit_throwsException() {
        String testInput = "[D][X] deasdfasdf (by: 2024-13-13 10:10)";
        try {
            Task res = new Storage().getTaskFromLine(testInput);
            fail();
        } catch (Exception e) {
            assertEquals("Text '2024-13-13' could not be parsed: Invalid value for MonthOfYear" +
                    " (valid values 1 - 12): 13", e.getMessage());
        }
    }

    @Test
    public void getTaskFromLine_deadlineDateWrongFormat_throwsException() {
        String testInput = "[D][X] deasdfasdf (by: 2024-1-1 10:10)";
        try {
            Task res = new Storage().getTaskFromLine(testInput);
            fail();
        } catch (Exception e) {
            assertEquals("Error while parsing deadline: deasdfasdf (by: 2024-1-1 10:10)" , e.getMessage());
        }
    }

    @Test
    public void getTaskFromLine_deadlineTimeOverLimit_throwsException() {
        String testInput = "[D][X] deasdfasdf (by: 2024-01-01 24:01)";
        try {
            Task res = new Storage().getTaskFromLine(testInput);
            fail();
        } catch (Exception e) {
            assertEquals("Text '24:01' could not be parsed: Invalid value for HourOfDay " +
                    "(valid values 0 - 23): 24" , e.getMessage());
        }
    }

    @Test
    public void getTaskFromLine_deadlineTimeWrongFormat_throwsException() {
        String testInput = "[D][X] deasdfasdf (by: 2024-01-01 1232)";
        try {
            Task res = new Storage().getTaskFromLine(testInput);
            fail();
        } catch (Exception e) {
            assertEquals("Error while parsing deadline: deasdfasdf (by: 2024-01-01 1232)" , e.getMessage());
        }
    }

    @Test
    public void getTaskFromLine_validEvent_success() throws RegexMatchFailureException, InvalidTaskTypeException {
        String testInput = "[E][ ] my event (from: 2024-01-01 10:00 to: 2024-10-10 01:01)";
        Task res = new Storage().getTaskFromLine(testInput);
        assertEquals(res.toStringWrite(), testInput);
    }

    @Test
    public void getTaskFromLine_eventBeginTimeWrongFormat_throwsException() {
        String testInput = "[E][ ] my event (from: 2024-01-01 1000 to: 2024-10-10 01:01)";
        try {
            Task res = new Storage().getTaskFromLine(testInput);
            fail();
        } catch (Exception e) {
            assertEquals("Error while parsing event: my event (from: 2024-01-01 1000 to: 2024-10-10 01:01)",
                    e.getMessage());
        }
    }

    @Test
    public void getTaskFromLine_eventEndTimeWrongFormat_throwsException() {
        String testInput = "[E][ ] my event (from: 2024-01-01 10:00 to: 2024-10-10 0101)";
        try {
            Task res = new Storage().getTaskFromLine(testInput);
            fail();
        } catch (Exception e) {
            assertEquals("Error while parsing event: my event (from: 2024-01-01 10:00 to: 2024-10-10 0101)",
                    e.getMessage());
        }
    }

    @Test
    public void getTaskFromLine_eventEndTimeWrongValue_throwsException() {
        String testInput = "[E][ ] my event (from: 2024-01-01 10:00 to: 2024-10-10 91:01)";
        try {
            Task res = new Storage().getTaskFromLine(testInput);
            fail();
        } catch (Exception e) {
            assertEquals("Text '91:01' could not be parsed: Invalid value" +
                            " for HourOfDay (valid values 0 - 23): 91", e.getMessage());
        }
    }

    @Test
    public void getTaskFromLine_eventStartTimeWrongValue_throwsException() {
        String testInput = "[E][ ] my event (from: 2024-01-01 90:00 to: 2024-10-10 11:01)";
        try {
            Task res = new Storage().getTaskFromLine(testInput);
            fail();
        } catch (Exception e) {
            assertEquals("Text '90:00' could not be parsed: Invalid value" +
                    " for HourOfDay (valid values 0 - 23): 90", e.getMessage());
        }
    }

    @Test
    public void getTaskFromLine_eventStartDateWrongFormat_throwsException() {
        String testInput = "[E][ ] my event (from: 2024-1-01 90:00 to: 2024-10-10 11:01)";
        try {
            Task res = new Storage().getTaskFromLine(testInput);
            fail();
        } catch (Exception e) {
            assertEquals("Error while parsing event: my event (from: 2024-1-01 90:00 to:" +
                    " 2024-10-10 11:01)", e.getMessage());
        }
    }
}
