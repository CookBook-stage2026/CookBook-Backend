package cookbook.stage.backend.recipe.api.dto;

import cookbook.stage.backend.recipe.domain.Recipe;

import java.util.List;
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
    public static RecipeDto fromDomain(Recipe recipe) {
        List<RecipeIngredientDto> recipeIngredientDtos = recipe.getIngredients().stream()
                .map(i -> new RecipeIngredientDto(i.ingredientId().id(), i.baseQuantity()))
                .toList();

        return new RecipeDto(
                recipe.getId().id(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getDurationInMinutes(),
                recipe.getSteps(),
                recipeIngredientDtos,
                recipe.getServings()
        );
    }
}
