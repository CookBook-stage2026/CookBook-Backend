package cookbook.stage.backend.recipe.domain.recipe;

import cookbook.stage.backend.recipe.domain.ingredient.Ingredient;
import cookbook.stage.backend.recipe.domain.ingredient.IngredientId;
import cookbook.stage.backend.recipe.domain.ingredient.Unit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipeIngredientTest {

    private static final Ingredient VALID_INGREDIENT = new Ingredient(IngredientId.create(), "Ingredient", Unit.GRAM);
    private static final double VALID_QUANTITY = 1.5;

    @Test
    void constructor_shouldCreateRecipeIngredient_whenValidParameters() {
        // Act
        RecipeIngredient recipeIngredient = new RecipeIngredient(VALID_INGREDIENT, VALID_QUANTITY);

        // Assert
        assertThat(recipeIngredient.ingredient()).isEqualTo(VALID_INGREDIENT);
        assertThat(recipeIngredient.baseQuantity()).isEqualTo(VALID_QUANTITY);
    }

    @Test
    void constructor_shouldThrowException_whenIngredientIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> new RecipeIngredient(null, VALID_QUANTITY))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Ingredient cannot be null!");
    }

    @Test
    void constructor_shouldThrowException_whenBaseQuantityIsNegative() {
        // Act & Assert
        assertThatThrownBy(() -> new RecipeIngredient(VALID_INGREDIENT, -1.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Base quantity must be greater than 0!");
    }
}
