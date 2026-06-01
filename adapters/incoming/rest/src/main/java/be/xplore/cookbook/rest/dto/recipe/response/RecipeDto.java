package be.xplore.cookbook.rest.dto.recipe.response;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeIngredient;
import be.xplore.cookbook.core.domain.user.UserId;

import java.util.Comparator;
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
                .sorted(Comparator.comparing(RecipeDto::highestPriorityCategory)
                        .thenComparingDouble(RecipeIngredient::baseQuantity)
                        .thenComparing(ri -> ri.ingredient().name()))
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

    private static Category highestPriorityCategory(RecipeIngredient recipeIngredient) {
        return recipeIngredient.ingredient().categories().stream()
                .min(Comparator.comparingInt(Category::ordinal))
                .orElse(Category.values()[Category.values().length - 1]);
    }
}
