package be.xplore.cookbook.core.domain.recipe.command;

import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.recipe.RecipeDetails;
import be.xplore.cookbook.core.domain.user.UserId;

import java.util.Map;

public record CreateRecipeCommand(RecipeDetails details,
                                  Map<IngredientId, Double> ingredientQuantities, UserId userId) {
}
