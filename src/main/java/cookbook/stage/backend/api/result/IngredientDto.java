package cookbook.stage.backend.api.result;

import cookbook.stage.backend.domain.ingredient.Category;
import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.Unit;

import java.util.List;
import java.util.UUID;

public record IngredientDto(
        UUID id,
        String name,
        Unit unit,
        List<Category> categories
) {
    public static IngredientDto fromDomain(Ingredient ingredient) {
        return new IngredientDto(
                ingredient.id().id(),
                ingredient.name(),
                ingredient.unit(),
                ingredient.categories()
        );
    }
}
