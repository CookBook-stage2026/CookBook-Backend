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
        List<IngredientDto> ingredients
) {
    public static RecipeDto fromDomain(Recipe recipe) {
        List<IngredientDto> ingredientDtos = recipe.getIngredients().stream()
                .map(i -> new IngredientDto(i.name(), i.quantity(), i.unit()))
                .toList();

        return new RecipeDto(
                recipe.getId().id(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getDurationInMinutes(),
                recipe.getSteps(),
                ingredientDtos
        );
    }
}
