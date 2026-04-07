package cookbook.stage.backend.recipe.domain;

import cookbook.stage.backend.ingredient.shared.IngredientId;
import cookbook.stage.backend.recipe.shared.RecipeId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipeTest {

    private static final RecipeId VALID_ID = RecipeId.create();
    private static final String VALID_NAME = "Pasta";
    private static final String VALID_DESCRIPTION = "Delicious pasta";
    private static final int VALID_DURATION = 30;
    private static final List<String> VALID_STEPS = List.of("Boil water", "Add pasta");
    private static final List<RecipeIngredient> VALID_INGREDIENTS = List.of(
            new RecipeIngredient(new IngredientId(UUID.randomUUID()), 200.0)
    );
    private static final int VALID_SERVINGS = 2;

    @Test
    void constructor_shouldCreateRecipe_whenValidParameters() {
        // Act
        Recipe recipe = new Recipe(VALID_ID, VALID_NAME, VALID_DESCRIPTION, VALID_DURATION,
                VALID_STEPS, VALID_INGREDIENTS, VALID_SERVINGS);

        // Assert
        assertThat(recipe.getId()).isEqualTo(VALID_ID);
        assertThat(recipe.getName()).isEqualTo(VALID_NAME);
        assertThat(recipe.getDescription()).isEqualTo(VALID_DESCRIPTION);
        assertThat(recipe.getDurationInMinutes()).isEqualTo(VALID_DURATION);
        assertThat(recipe.getSteps()).isEqualTo(VALID_STEPS);
        assertThat(recipe.getIngredients()).isEqualTo(VALID_INGREDIENTS);
        assertThat(recipe.getServings()).isEqualTo(VALID_SERVINGS);
    }

    @Test
    void constructor_shouldSetServingsToOne_whenServingsIsNegative() {
        // Act
        Recipe recipe = new Recipe(VALID_ID, VALID_NAME, VALID_DESCRIPTION, VALID_DURATION,
                VALID_STEPS, VALID_INGREDIENTS, -1);

        // Assert
        assertThat(recipe.getServings()).isEqualTo(1);
    }

    @Test
    void constructor_shouldThrowException_whenNameIsBlank() {
        // Act & Assert
        assertThatThrownBy(() -> new Recipe(VALID_ID, "", VALID_DESCRIPTION, VALID_DURATION,
                VALID_STEPS, VALID_INGREDIENTS, VALID_SERVINGS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A recipe must have a name");
    }

    @Test
    void constructor_shouldThrowException_whenDescriptionIsBlank() {
        // Act & Assert
        assertThatThrownBy(() -> new Recipe(VALID_ID, VALID_NAME, "", VALID_DURATION,
                VALID_STEPS, VALID_INGREDIENTS, VALID_SERVINGS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A recipe must have a description");
    }

    @Test
    void constructor_shouldThrowException_whenDurationIsNegative() {
        // Act & Assert
        assertThatThrownBy(() -> new Recipe(VALID_ID, VALID_NAME, VALID_DESCRIPTION, -1,
                VALID_STEPS, VALID_INGREDIENTS, VALID_SERVINGS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Duration must be greater than 0");
    }

    @Test
    void constructor_shouldThrowException_whenIngredientsIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> new Recipe(VALID_ID, VALID_NAME, VALID_DESCRIPTION, VALID_DURATION,
                VALID_STEPS, null, VALID_SERVINGS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A recipe must have at least one ingredient");
    }
}
