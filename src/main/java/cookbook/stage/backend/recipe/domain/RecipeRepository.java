package cookbook.stage.backend.recipe.domain;

import cookbook.stage.backend.recipe.shared.RecipeId;

import java.util.Optional;

public interface RecipeRepository {
    Recipe save(Recipe recipe);

    Optional<Recipe> findById(RecipeId id);
}
