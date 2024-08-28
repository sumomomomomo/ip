package joeduck.exception;

/**
 * Exception for when user inputs an unrecognized command.
 */
public class InvalidCommandException extends JoeDuckException {
    public InvalidCommandException(String msg) {
        super(msg);
    }
}
