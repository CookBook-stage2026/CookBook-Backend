package be.xplore.cookbook.rest.dto.recipe.response;

import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.recipe.RecipeIngredient;

import java.util.List;
import java.util.UUID;

public record RecipeIngredientDto(
        UUID ingredientId,
        String name,
        Unit unit,
        double quantity,
        List<MacroDto> macros
) {
    public static RecipeIngredientDto fromDomain(RecipeIngredient recipeIngredient) {
        List<MacroDto> scaledMacros = recipeIngredient.ingredient().macros().stream()
                .map(m -> MacroDto.fromDomain(m, recipeIngredient.baseQuantity()))
                .toList();

        return new RecipeIngredientDto(
                recipeIngredient.ingredient().id().id(),
                recipeIngredient.ingredient().name(),
                recipeIngredient.ingredient().unit(),
                recipeIngredient.baseQuantity(),
                scaledMacros
        );
    }
}
