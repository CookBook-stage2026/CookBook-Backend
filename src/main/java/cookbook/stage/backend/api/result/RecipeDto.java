package cookbook.stage.backend.api.result;

import cookbook.stage.backend.domain.recipe.Recipe;

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
        List<RecipeIngredientDto> ingredientDtos = recipe.getIngredients().stream()
                .map(RecipeIngredientDto::fromDomain)
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
