package be.xplore.cookbook.core.domain.recipe;

import be.xplore.cookbook.core.domain.user.User;

public record RecipeSummary(
        RecipeId id,
        String name,
        String description,
        int durationInMinutes,
        User user
) {
}
