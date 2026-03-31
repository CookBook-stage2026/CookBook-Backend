package cookbook.stage.backend.user.domain;

import cookbook.stage.backend.recipe.shared.RecipeId;
import cookbook.stage.backend.user.shared.UserId;
import org.jmolecules.ddd.annotation.ValueObject;

import java.time.Instant;
import java.util.Objects;

@ValueObject
public record SavedRecipe(UserId userId, RecipeId recipeId, Instant savedAt) {
    public static SavedRecipe saveRecipe(UserId userId, RecipeId recipeId) {
        return new SavedRecipe(userId, recipeId, Instant.now());
    }

    public SavedRecipe {
        Objects.requireNonNull(userId, "User id cannot be null!");
        Objects.requireNonNull(recipeId, "Recipe id cannot be null!");
        Objects.requireNonNull(savedAt, "Saved at cannot be null!");
    }
}
