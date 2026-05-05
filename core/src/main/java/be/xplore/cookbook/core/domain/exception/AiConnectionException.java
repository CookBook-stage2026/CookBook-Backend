package be.xplore.cookbook.core.domain.exception;

public class AiConnectionException extends RuntimeException {
    public AiConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
