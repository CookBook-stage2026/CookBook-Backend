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
        List<RecipeIngredientDto> ingredients
) {
    public static RecipeDto fromDomain(Recipe recipe) {
        return new RecipeDto(
                recipe.getId().id(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getDurationInMinutes(),
                recipe.getSteps(),
                recipe.getIngredients().stream()
                        .map(RecipeIngredientDto::fromDomain)
                        .toList()
        );
    }
}
