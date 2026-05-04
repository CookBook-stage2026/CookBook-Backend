package be.xplore.cookbook.rest.dto.response;

import be.xplore.cookbook.core.domain.ingredient.Ingredient;

import java.util.UUID;

public record IngredientSummaryDto(
        UUID id,
        String name
) {
    public static IngredientSummaryDto fromDomain(Ingredient ingredient) {
        return new IngredientSummaryDto(
                ingredient.id().id(),
                ingredient.name()
        );
    }
}
