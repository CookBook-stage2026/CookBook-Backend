package be.xplore.cookbook.ai.exception;

public class AiInvalidResponseException extends RuntimeException {
    public AiInvalidResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
