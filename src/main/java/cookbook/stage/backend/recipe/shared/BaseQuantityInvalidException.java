package cookbook.stage.backend.recipe.shared;

public class BaseQuantityInvalidException extends RuntimeException {
    public BaseQuantityInvalidException(String message) {
        super(message);
    }
}
