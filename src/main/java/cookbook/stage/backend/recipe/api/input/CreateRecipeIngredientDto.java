package cookbook.stage.backend.recipe.api.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateRecipeIngredientDto(
        @NotNull UUID ingredientId,
        @Positive double baseQuantity
) {
}
