package cookbook.stage.backend.recipe.domain;

import cookbook.stage.backend.ingredient.shared.IngredientId;
import cookbook.stage.backend.recipe.shared.BaseQuantityInvalidException;
import cookbook.stage.backend.recipe.shared.RecipeId;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public record RecipeIngredient(RecipeId recipeId, IngredientId ingredientId, boolean isScalable, double baseQuantity) {
    public RecipeIngredient {
        Objects.requireNonNull(recipeId, "Recipe id cannot be null!");
        Objects.requireNonNull(ingredientId, "Ingredient id cannot be null!");
        if (baseQuantity <= 0) {
            throw new BaseQuantityInvalidException("Base quantity must be greater than 0!");
        }
    }

    public double calculateQuantity(double multiplier) {
        if (!isScalable) {
            return baseQuantity;
        }
        return (multiplier / baseQuantity) * baseQuantity;
    }
}
