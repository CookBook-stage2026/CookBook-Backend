package cookbook.stage.backend.api.result;

import cookbook.stage.backend.domain.ingredient.Ingredient;

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
