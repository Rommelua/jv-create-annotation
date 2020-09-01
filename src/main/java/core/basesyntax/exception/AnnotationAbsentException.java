package core.basesyntax.exception;

public class AnnotationAbsentException extends RuntimeException {
    public AnnotationAbsentException(String message) {
        super(message);
    }

    public AnnotationAbsentException(String message, Throwable cause) {
        super(message, cause);
    }
}
