package be.xplore.cookbook.rest.dto.recipe.response;

import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.recipe.RecipeIngredient;

import java.util.UUID;

public record RecipeIngredientDto(
        UUID ingredientId,
        String name,
        Unit unit,
        double quantity
) {
    public static RecipeIngredientDto fromDomain(RecipeIngredient recipeIngredient) {
        return new RecipeIngredientDto(
                recipeIngredient.ingredient().id().id(),
                recipeIngredient.ingredient().name(),
                recipeIngredient.unit(),
                recipeIngredient.baseQuantity()
        );
    }
}
