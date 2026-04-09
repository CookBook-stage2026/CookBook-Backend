package cookbook.stage.backend.recipe.domain;

import cookbook.stage.backend.ingredient.shared.IngredientId;

import java.util.Objects;

public record RecipeIngredient(IngredientId ingredientId, double baseQuantity) {
    public RecipeIngredient {
        Objects.requireNonNull(ingredientId, "Ingredient id cannot be null!");
        if (baseQuantity <= 0) {
            throw new IllegalArgumentException("Base quantity must be greater than 0!");
        }
    }
}
