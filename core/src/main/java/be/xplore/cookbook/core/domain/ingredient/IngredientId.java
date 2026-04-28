package be.xplore.cookbook.core.domain.ingredient;

import java.util.Objects;
import java.util.UUID;

public record IngredientId(UUID id) {
    public IngredientId {
        Objects.requireNonNull(id, "Ingredient id cannot be null!");
    }

    public static IngredientId create() {
        return new IngredientId(UUID.randomUUID());
    }
}
