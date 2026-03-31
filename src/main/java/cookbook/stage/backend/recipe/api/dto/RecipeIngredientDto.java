package cookbook.stage.backend.recipe.api.dto;

import cookbook.stage.backend.ingredient.shared.IngredientId;
import cookbook.stage.backend.recipe.domain.RecipeIngredient;
import cookbook.stage.backend.recipe.shared.RecipeId;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RecipeIngredientDto(
        @NotNull UUID ingredientId,
        boolean isScalable,
        double baseQuantity
) {
    public static RecipeIngredientDto fromDomain(RecipeIngredient recipeIngredient) {
        return new RecipeIngredientDto(
                recipeIngredient.ingredientId().id(),
                recipeIngredient.isScalable(),
                recipeIngredient.baseQuantity()
        );
    }

    public RecipeIngredient toDomain(RecipeId recipeId) {
        return new RecipeIngredient(
                recipeId,
                new IngredientId(ingredientId),
                isScalable,
                baseQuantity
        );
    }
}
