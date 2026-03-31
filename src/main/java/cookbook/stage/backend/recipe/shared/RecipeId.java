package cookbook.stage.backend.recipe.shared;

import java.util.Objects;
import java.util.UUID;

public record RecipeId(UUID id) {
    public RecipeId {
        Objects.requireNonNull(id, "Recipe id cannot be null!");
    }

    public static RecipeId create() {
        return new RecipeId(UUID.randomUUID());
    }
}
