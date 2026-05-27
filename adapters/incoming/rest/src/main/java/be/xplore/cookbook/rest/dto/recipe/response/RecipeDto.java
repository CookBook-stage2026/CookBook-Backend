package be.xplore.cookbook.rest.dto.recipe.response;

import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.UserId;

import java.util.List;
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
        List<MacroDto> totalMacros = recipe.getMacros().stream()
                .map(MacroDto::fromDomain)
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
