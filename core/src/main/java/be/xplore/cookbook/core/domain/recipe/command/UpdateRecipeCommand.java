package be.xplore.cookbook.core.domain.recipe.command;

import be.xplore.cookbook.core.domain.recipe.RecipeDetails;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.user.UserId;

import java.util.List;

public record UpdateRecipeCommand(RecipeId id, RecipeDetails details,
                                  List<IngredientWithQuantity> ingredientQuantities, UserId userId) {
}
