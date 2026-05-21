package be.xplore.cookbook.core.domain.recipe.command;

import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.user.UserId;

public record DeleteRecipeCommand(RecipeId recipeId, UserId userId) {
}
