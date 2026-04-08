package cookbook.stage.backend.recipe.api.dto;

import cookbook.stage.backend.ingredient.shared.Unit;

import java.util.UUID;

public record RecipeIngredientDto(
        UUID ingredientId,
        String name,
        double baseQuantity,
        Unit unit
) {
}
