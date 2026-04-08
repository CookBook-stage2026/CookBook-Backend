package cookbook.stage.backend.recipe.api.dto;

import cookbook.stage.backend.ingredient.shared.IngredientApiDto;
import cookbook.stage.backend.recipe.application.RecipeWithIngredients;
import cookbook.stage.backend.recipe.domain.Recipe;
import cookbook.stage.backend.shared.domain.DataIntegrityException;

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
    public static RecipeDto fromDomain(RecipeWithIngredients data) {
        List<RecipeIngredientDto> ingredientDtos = data.recipe().getIngredients().stream()
                .map(ri -> {
                    IngredientApiDto ingredient = data.ingredientMap().get(ri.ingredientId());

                    if (ingredient == null) {
                        throw new DataIntegrityException(
                                "Recipe references ingredient " + ri.ingredientId().id() + " which no longer exists"
                        );
                    }

                    return new RecipeIngredientDto(
                            ingredient.id().id(),
                            ingredient.name(),
                            ri.baseQuantity(),
                            ingredient.unit()
                    );
                })
                .toList();

        Recipe recipe = data.recipe();

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
