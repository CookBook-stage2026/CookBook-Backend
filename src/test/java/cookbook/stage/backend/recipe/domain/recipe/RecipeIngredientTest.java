package cookbook.stage.backend.recipe.domain.recipe;

import cookbook.stage.backend.recipe.domain.ingredient.IngredientId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipeIngredientTest {

    private static final IngredientId VALID_ID = new IngredientId(UUID.randomUUID());
    private static final double VALID_QUANTITY = 1.5;

    @Test
    void constructor_shouldCreateRecipeIngredient_whenValidParameters() {
        // Act
        RecipeIngredient recipeIngredient = new RecipeIngredient(VALID_ID, VALID_QUANTITY);

        // Assert
        assertThat(recipeIngredient.ingredientId()).isEqualTo(VALID_ID);
        assertThat(recipeIngredient.baseQuantity()).isEqualTo(VALID_QUANTITY);
    }

    @Test
    void constructor_shouldThrowException_whenIngredientIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> new RecipeIngredient(null, VALID_QUANTITY))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Ingredient id cannot be null!");
    }

    @Test
    void constructor_shouldThrowException_whenBaseQuantityIsNegative() {
        // Act & Assert
        assertThatThrownBy(() -> new RecipeIngredient(VALID_ID, -1.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Base quantity must be greater than 0!");
    }
}
