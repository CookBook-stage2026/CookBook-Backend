package cookbook.stage.backend.ingredient.api.dto;

import cookbook.stage.backend.ingredient.domain.Ingredient;
import cookbook.stage.backend.ingredient.domain.Unit;

import java.util.UUID;

public record IngredientDto(
        UUID id,
        String name,
        Unit unit
) {
    public static IngredientDto fromDomain(Ingredient ingredient) {
        return new IngredientDto(
                ingredient.id().id(),
                ingredient.name(),
                ingredient.unit()
        );
    }
}
