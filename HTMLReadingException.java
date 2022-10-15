package jarachnea;

public class HTMLReadingException extends Exception {

    public HTMLReadingException(final String message) {
        super(message);
    }

    public HTMLReadingException(final String message, Throwable cause) {
        super(message, cause);
    }
}
