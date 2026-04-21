package cookbook.stage.backend.api.result;

import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.Unit;

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
