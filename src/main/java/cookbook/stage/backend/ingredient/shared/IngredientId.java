package cookbook.stage.backend.ingredient.shared;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record IngredientId(UUID id) {
    public IngredientId {
        Objects.requireNonNull(id, "Ingredient id cannot be null!");
    }

    public static IngredientId create() {
        return new IngredientId(UUID.randomUUID());
    }
}
