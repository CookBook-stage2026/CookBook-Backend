package be.xplore.cookbook.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateRecipeIngredientDto(
        @NotNull UUID ingredientId,
        @Positive double baseQuantity
) {
}
