package joeduck.exception;

/**
 * Exception for when a Todo is attempted to be created with no description.
 */
public class EmptyTodoException extends JoeDuckException {
    public EmptyTodoException(String msg) {
        super(msg);
    }
}
