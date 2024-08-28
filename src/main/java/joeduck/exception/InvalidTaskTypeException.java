package joeduck.exception;

/**
 * Exception when Parser sees an unrecognized task type.
 */
public class InvalidTaskTypeException extends JoeDuckException {
    public InvalidTaskTypeException(String msg) {
        super(msg);
    }
}
