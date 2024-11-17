package exceptions;

public class NoSuchEpicExistsException extends RuntimeException {

    public NoSuchEpicExistsException(String message) {
        super(message);
    }
}
