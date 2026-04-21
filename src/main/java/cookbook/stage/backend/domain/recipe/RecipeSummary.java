package cookbook.stage.backend.domain.recipe;

public record RecipeSummary(
        RecipeId id,
        String name,
        String description,
        int durationInMinutes
) {
}
