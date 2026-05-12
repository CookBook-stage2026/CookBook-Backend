package be.xplore.cookbook.core.domain.exception;

public class RecipeImportException extends RuntimeException {
    public RecipeImportException(String message) {
        super(message);
    }

    public RecipeImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
