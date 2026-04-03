package cookbook.stage.backend.recipe.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record RecipeIngredientDto(
        @NotNull UUID recipeId,
        @NotNull UUID ingredientId,
        @Positive double baseQuantity
) {
}
