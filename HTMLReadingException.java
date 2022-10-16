package jarachnea;

public final class HTMLReadingException extends Exception {

    public HTMLReadingException(final String message) {
        super(message);
    }

    public HTMLReadingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
