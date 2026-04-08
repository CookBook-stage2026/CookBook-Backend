package cookbook.stage.backend.ingredient.domain;

import cookbook.stage.backend.ingredient.shared.IngredientId;
import cookbook.stage.backend.ingredient.shared.Unit;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IngredientTest {

    private static final IngredientId VALID_ID = new IngredientId(UUID.randomUUID());
    private static final String VALID_NAME = "Flour";
    private static final Unit VALID_UNIT = Unit.GRAM;

    @Test
    void constructor_shouldCreateIngredient_whenValidParameters() {
        // Act
        Ingredient ingredient = new Ingredient(VALID_ID, VALID_NAME, VALID_UNIT);

        // Assert
        assertThat(ingredient.id()).isEqualTo(VALID_ID);
        assertThat(ingredient.name()).isEqualTo(VALID_NAME);
        assertThat(ingredient.unit()).isEqualTo(VALID_UNIT);
    }

    @Test
    void constructor_shouldThrowException_whenIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> new Ingredient(null, VALID_NAME, VALID_UNIT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ingredient id cannot be null!");
    }

    @Test
    void constructor_shouldThrowException_whenNameIsBlank() {
        // Act & Assert
        assertThatThrownBy(() -> new Ingredient(VALID_ID, "   ", VALID_UNIT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ingredient name cannot be null or blank!");
    }

    @Test
    void constructor_shouldThrowException_whenUnitIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> new Ingredient(VALID_ID, VALID_NAME, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ingredient unit cannot be null!");
    }
}
