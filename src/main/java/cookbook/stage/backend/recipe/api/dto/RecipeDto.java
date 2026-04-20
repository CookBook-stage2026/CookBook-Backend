package cookbook.stage.backend.recipe.api.dto;

import cookbook.stage.backend.recipe.domain.ingredient.Ingredient;
import cookbook.stage.backend.recipe.domain.ingredient.IngredientId;
import cookbook.stage.backend.recipe.domain.recipe.Recipe;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record RecipeDto(
        UUID id,
        String name,
        String description,
        int durationInMinutes,
        List<String> steps,
        List<RecipeIngredientDto> ingredients,
        int servings
) {
    public static RecipeDto fromDomain(Recipe recipe, Map<IngredientId, Ingredient> ingredientMap) {
        List<RecipeIngredientDto> ingredientDtos = recipe.getIngredients().stream()
                .map(ri -> {
                    Ingredient ingredient = ingredientMap.get(ri.ingredientId());
                    return new RecipeIngredientDto(
                            ingredient.id().id(),
                            ingredient.name(),
                            ri.baseQuantity(),
                            ingredient.unit()
                    );
                })
                .toList();

        return new RecipeDto(
                recipe.getId().id(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getDurationInMinutes(),
                recipe.getSteps(),
                ingredientDtos,
                recipe.getServings()
        );
    }
}
