package cookbook.stage.backend.domain.ingredient;

import cookbook.stage.backend.domain.exception.NotFoundException;

import java.util.Objects;
import java.util.UUID;

public record IngredientId(UUID id) {
    public IngredientId {
        Objects.requireNonNull(id, "Ingredient id cannot be null!");
    }

    public NotFoundException notFound() {
        return new NotFoundException("Ingredient [" + id + "] not found");
    }

    public static IngredientId create() {
        return new IngredientId(UUID.randomUUID());
    }
}
