package cookbook.stage.backend.domain.exception;

public class DataIntegrityException extends RuntimeException {
    public DataIntegrityException(String message) {
        super(message);
    }
}
