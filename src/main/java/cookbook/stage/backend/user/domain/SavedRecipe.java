package cookbook.stage.backend.user.domain;

import cookbook.stage.backend.recipe.shared.RecipeId;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public record SavedRecipe(UserId userId, RecipeId recipeId) {
    public SavedRecipe{
        Objects.requireNonNull(userId, "User id cannot be null!");
        Objects.requireNonNull(recipeId, "Recipe id cannot be null!");
    }
}
