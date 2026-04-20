package cookbook.stage.backend.recipe.domain.recipe;

public record RecipeSummary(
        RecipeId id,
        String name,
        String description,
        int durationInMinutes
) {
}
