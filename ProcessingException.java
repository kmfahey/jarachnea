package jarachnea;


public final class ProcessingException extends Exception {

    public ProcessingException(final String message) {
        super(message);
    }

    public ProcessingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
