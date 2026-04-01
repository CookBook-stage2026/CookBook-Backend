package cookbook.stage.backend.recipe.api.dto;

import cookbook.stage.backend.recipe.domain.Recipe;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public record RecipeDto(
        UUID id,
        String name,
        String description,
        int durationInMinutes,
        List<String> steps,
        Map<String, IngredientDto> ingredients
) {
    public static RecipeDto fromDomain(Recipe recipe) {
        Map<String, IngredientDto> ingredientDtos = recipe.getIngredients().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new IngredientDto(e.getKey(), e.getValue().quantity(), e.getValue().unit())
                ));

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
