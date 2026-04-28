package be.xplore.cookbook.core.domain.recipe;

public record RecipeSummary(
        RecipeId id,
        String name,
        String description,
        int durationInMinutes
) {
}
