package be.xplore.cookbook.rest.dto.recipe.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record NewRecipeIngredientDto(
        @NotNull UUID ingredientId,
        @Positive double baseQuantity
) {
}
