package be.xplore.cookbook.core.domain.recipe.command;

import be.xplore.cookbook.core.common.Paging;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.user.UserId;

import java.util.List;

public record FilterRecipesQuery(List<IngredientId> ingredientIds,
                          Paging paging, boolean shouldApplyPreferences, UserId userId) {
}
