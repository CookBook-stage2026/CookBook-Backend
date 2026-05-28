package be.xplore.cookbook.core.domain.recipe.command;

import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.ingredient.Unit;

public record IngredientWithQuantity(
        IngredientId ingredientId,
        double quantity,
        Unit unit
) {
}
