package joeduck.exception;

/**
 * Exception thrown when Storage fails to load.
 */
public class StorageLoadException extends JoeDuckException {
    public StorageLoadException(String msg) {
        super(msg);
    }
}
