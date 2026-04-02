package cookbook.stage.backend.recipe.domain;

import cookbook.stage.backend.recipe.shared.RecipeId;

public record RecipeSummary(
        RecipeId id,
        String name,
        String description,
        int durationInMinutes
) {
}
