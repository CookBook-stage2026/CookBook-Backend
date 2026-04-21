package cookbook.stage.backend.domain.recipe;

import cookbook.stage.backend.domain.exception.NotFoundException;

import java.util.Objects;
import java.util.UUID;

public record RecipeId(UUID id) {
    public RecipeId {
        Objects.requireNonNull(id, "Recipe id cannot be null!");
    }

    public NotFoundException notFound() {
        return new NotFoundException("Recipe [" + id + "] not found");
    }

    public static RecipeId create() {
        return new RecipeId(UUID.randomUUID());
    }
}
