package cookbook.stage.backend.ingredient.shared;

import cookbook.stage.backend.ingredient.domain.Ingredient;

public record IngredientApiDto(
        IngredientId id,
        String name,
        Unit unit
) {
    public static IngredientApiDto fromDomain(Ingredient ingredient) {
        return new IngredientApiDto(
                ingredient.id(),
                ingredient.name(),
                ingredient.unit()
        );
    }
}
