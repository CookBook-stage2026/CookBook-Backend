package cookbook.stage.backend.recipe.application;

import cookbook.stage.backend.ingredient.shared.IngredientApiDto;
import cookbook.stage.backend.ingredient.shared.IngredientId;
import cookbook.stage.backend.recipe.domain.Recipe;

import java.util.Map;

public record RecipeWithIngredients(
        Recipe recipe,
        Map<IngredientId, IngredientApiDto> ingredientMap
) {
}
