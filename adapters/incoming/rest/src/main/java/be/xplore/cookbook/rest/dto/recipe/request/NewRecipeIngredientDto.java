package be.xplore.cookbook.rest.dto.recipe.request;

import be.xplore.cookbook.core.domain.ingredient.Unit;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record NewRecipeIngredientDto(
        @NotNull UUID ingredientId,
        @Positive double baseQuantity,
        @NotNull Unit unit
) {
}
