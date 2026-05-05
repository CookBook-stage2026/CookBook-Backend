package be.xplore.cookbook.core.domain.recipe.command;

import be.xplore.cookbook.core.domain.recipe.RecipeDetails;
import be.xplore.cookbook.core.domain.user.UserId;

import java.util.List;

public record CreateRecipeCommand(RecipeDetails details,
                                  List<IngredientWithQuantity> ingredientQuantities, UserId userId) {
}
