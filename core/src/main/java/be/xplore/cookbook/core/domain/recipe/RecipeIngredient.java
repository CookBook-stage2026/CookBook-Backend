package be.xplore.cookbook.core.domain.recipe;

import be.xplore.cookbook.core.domain.ingredient.Ingredient;

import java.util.Objects;

public record RecipeIngredient(Ingredient ingredient, double baseQuantity) {
    public RecipeIngredient {
        Objects.requireNonNull(ingredient, "Ingredient cannot be null!");
        if (baseQuantity <= 0) {
            throw new IllegalArgumentException("Base quantity must be greater than 0!");
        }
    }

    public static RecipeIngredient merge(RecipeIngredient a, RecipeIngredient b) {
        if (!a.ingredient.equals(b.ingredient)) {
            throw new IllegalArgumentException("Cannot merge ingredients with different IDs");
        }
        return new RecipeIngredient(a.ingredient, a.baseQuantity + b.baseQuantity);
    }
}
