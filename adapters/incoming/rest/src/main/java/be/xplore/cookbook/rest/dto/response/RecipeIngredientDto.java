package be.xplore.cookbook.rest.dto.response;

import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.recipe.RecipeIngredient;

import java.util.UUID;

public record RecipeIngredientDto(
        UUID ingredientId,
        String name,
        double baseQuantity,
        Unit unit
) {
    public static RecipeIngredientDto fromDomain(RecipeIngredient ri) {
        Ingredient ingredient = ri.ingredient();
        return new RecipeIngredientDto(
                ingredient.id().id(),
                ingredient.name(),
                ri.baseQuantity(),
                ingredient.unit()
        );
    }
}
