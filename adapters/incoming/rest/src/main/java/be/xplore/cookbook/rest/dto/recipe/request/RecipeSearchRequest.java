package be.xplore.cookbook.rest.dto.recipe.request;

import be.xplore.cookbook.core.common.SortDirection;
import be.xplore.cookbook.core.domain.recipe.RecipeSortingOptions;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record RecipeSearchRequest(
        @NotNull List<UUID> ingredientIds,
        boolean shouldApplyPreferences,
        boolean includeAccessibleRecipes,
        int page,
        int size,
        @NotNull RecipeSortingOptions sortBy,
        @NotNull SortDirection sortDirection
) {
}
