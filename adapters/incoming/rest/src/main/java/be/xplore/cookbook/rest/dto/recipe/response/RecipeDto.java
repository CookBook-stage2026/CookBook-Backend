package be.xplore.cookbook.rest.dto.recipe.response;

import be.xplore.cookbook.core.domain.ingredient.MacroType;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.UserId;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record RecipeDto(
        UUID id,
        String name,
        String description,
        int durationInMinutes,
        int servings,
        List<String> steps,
        boolean isPublic,
        boolean isOwner,
        List<RecipeIngredientDto> ingredients,
        List<MacroDto> totalMacros
) {
    public static RecipeDto fromDomain(Recipe recipe, UserId requestingUserId) {
        Map<MacroType, Double> macros = recipe.calculateMacros();

        List<MacroDto> totalMacros = macros.entrySet().stream()
                .map(e -> MacroDto.of(e.getKey(), e.getValue()))
                .toList();

        List<RecipeIngredientDto> ingredientDtos = recipe.getIngredients().stream()
                .map(RecipeIngredientDto::fromDomain)
                .toList();

        return new RecipeDto(
                recipe.getId().id(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getDurationInMinutes(),
                recipe.getServings(),
                recipe.getSteps(),
                recipe.isPublic(),
                recipe.getUser().id().equals(requestingUserId),
                ingredientDtos,
                totalMacros
        );
    }
}
