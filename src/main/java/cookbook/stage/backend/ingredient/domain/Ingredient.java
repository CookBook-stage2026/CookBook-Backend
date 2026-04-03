package cookbook.stage.backend.ingredient.domain;

import cookbook.stage.backend.ingredient.shared.IngredientId;

public record Ingredient(
        IngredientId id,
        String name,
        Unit unit
) {
    public Ingredient {
        if (id == null) {
            throw new IllegalArgumentException("Ingredient id cannot be null!");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Ingredient name cannot be null or blank!");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Ingredient unit cannot be null!");
        }
    }
}
