package cookbook.stage.backend.api.result;

import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.Unit;
import cookbook.stage.backend.domain.recipe.RecipeIngredient;

import java.util.UUID;

public record RecipeIngredientDto(
        UUID ingredientId,
        String name,
        double baseQuantity,
        Unit unit
) {
    public static RecipeIngredientDto fromDomain(RecipeIngredient ri) {
        Ingredient ingredient = ri.ingredient();
        return new RecipeIngredientDto(
                ingredient.id().id(),
                ingredient.name(),
                ri.baseQuantity(),
                ingredient.unit()
        );
    }
}
