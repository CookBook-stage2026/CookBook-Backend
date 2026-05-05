package be.xplore.cookbook.core.domain.recipe.command;

import be.xplore.cookbook.core.domain.ingredient.IngredientId;

public record IngredientWithQuantity(
        IngredientId ingredientId,
        double quantity
) {
}
