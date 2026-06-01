package be.xplore.cookbook.core.domain.recipe.command;

import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.user.UserId;

public record GetRecipeForServingsQuery(RecipeId recipeId, int desiredServings, UserId userId) {
}
