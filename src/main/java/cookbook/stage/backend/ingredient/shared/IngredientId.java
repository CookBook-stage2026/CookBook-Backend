package cookbook.stage.backend.ingredient.shared;

import cookbook.stage.backend.shared.domain.NotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
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
