package cookbook.stage.backend.domain.recipe;

import cookbook.stage.backend.domain.ingredient.Ingredient;

import java.util.Objects;

public record RecipeIngredient(Ingredient ingredient, double baseQuantity) {
    public RecipeIngredient {
        Objects.requireNonNull(ingredient, "Ingredient cannot be null!");
        if (baseQuantity <= 0) {
            throw new IllegalArgumentException("Base quantity must be greater than 0!");
        }
    }
}
