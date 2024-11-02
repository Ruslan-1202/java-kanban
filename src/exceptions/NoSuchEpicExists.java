package exceptions;

public class NoSuchEpicExists extends RuntimeException {

    public NoSuchEpicExists(String message) {
        super(message);
    }
}
