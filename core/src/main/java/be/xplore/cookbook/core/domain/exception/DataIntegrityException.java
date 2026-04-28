package be.xplore.cookbook.core.domain.exception;

public class DataIntegrityException extends RuntimeException {
    public DataIntegrityException(String message) {
        super(message);
    }
}
