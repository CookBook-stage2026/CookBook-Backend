package cookbook.stage.backend.recipe.api.dto;

import cookbook.stage.backend.recipe.domain.ingredient.Unit;

import java.util.UUID;

public record RecipeIngredientDto(
        UUID ingredientId,
        String name,
        double baseQuantity,
        Unit unit
) {
}
