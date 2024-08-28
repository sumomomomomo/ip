package joeduck.exception;

/**
 * Exception thrown when Regex matching fails. Normally associated
 * with inputs involving dates, such as Event and Deadline.
 */
public class RegexMatchFailureException extends JoeDuckException {
    public RegexMatchFailureException(String msg) {
        super(msg);
    }
}
