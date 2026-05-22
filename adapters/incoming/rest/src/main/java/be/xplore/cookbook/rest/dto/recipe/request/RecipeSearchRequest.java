package be.xplore.cookbook.rest.dto.recipe.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record RecipeSearchRequest(
        @NotNull List<UUID> ingredientIds,
        boolean shouldApplyPreferences,
        boolean includeAccessibleRecipes,
        int page,
        int size
) {
}
