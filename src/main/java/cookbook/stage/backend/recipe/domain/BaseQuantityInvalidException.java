package cookbook.stage.backend.recipe.domain;

public class BaseQuantityInvalidException extends RuntimeException {
    public BaseQuantityInvalidException(String message) {
        super(message);
    }
}
