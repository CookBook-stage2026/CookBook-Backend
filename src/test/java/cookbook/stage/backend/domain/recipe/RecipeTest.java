package cookbook.stage.backend.domain.recipe;

import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.ingredient.Unit;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.repository.jpa.recipe.RecipeDetails;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipeTest {

    private static final RecipeId VALID_ID = RecipeId.create();
    private static final String VALID_NAME = "Pasta";
    private static final String VALID_DESCRIPTION = "Delicious pasta";
    private static final int VALID_DURATION = 30;
    private static final List<String> VALID_STEPS = List.of("Boil water", "Add pasta");
    private static final List<RecipeIngredient> VALID_INGREDIENTS = List.of(
            new RecipeIngredient(new Ingredient(IngredientId.create(), "Ingredient", Unit.GRAM), 200.0)
    );
    private static final int VALID_SERVINGS = 2;
    private static final UserId USER_ID = UserId.create();

    @Test
    void constructor_shouldCreateRecipe_whenValidParameters() {
        // Act
        Recipe recipe = new Recipe(
                VALID_ID,
                new RecipeDetails(
                        VALID_NAME,
                        VALID_DESCRIPTION,
                        VALID_DURATION,
                        VALID_SERVINGS,
                        VALID_STEPS,
                        VALID_INGREDIENTS
                ),
                USER_ID);

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
        // Act & Assert
        assertThatThrownBy(() -> new Recipe(
                VALID_ID,
                new RecipeDetails(
                        VALID_NAME,
                        VALID_DESCRIPTION,
                        VALID_DURATION,
                        -1,
                        VALID_STEPS,
                        VALID_INGREDIENTS
                ),
                USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Servings must be greater than 0");
    }

    @Test
    void constructor_shouldThrowException_whenNameIsBlank() {
        // Act & Assert
        assertThatThrownBy(() -> new Recipe(
                VALID_ID,
                new RecipeDetails(
                        "",
                        VALID_DESCRIPTION,
                        VALID_DURATION,
                        VALID_SERVINGS,
                        VALID_STEPS,
                        VALID_INGREDIENTS
                ),
                USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A recipe must have a name");
    }

    @Test
    void constructor_shouldThrowException_whenDescriptionIsBlank() {
        // Act & Assert
        assertThatThrownBy(() -> new Recipe(
                VALID_ID,
                new RecipeDetails(
                        VALID_NAME,
                        "",
                        VALID_DURATION,
                        VALID_SERVINGS,
                        VALID_STEPS,
                        VALID_INGREDIENTS
                ),
                USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A recipe must have a description");
    }

    @Test
    void constructor_shouldThrowException_whenDurationIsNegative() {
        // Act & Assert
        assertThatThrownBy(() -> new Recipe(
                VALID_ID,
                new RecipeDetails(
                        VALID_NAME,
                        VALID_DESCRIPTION,
                        -1,
                        VALID_SERVINGS,
                        VALID_STEPS,
                        VALID_INGREDIENTS
                ),
                USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Duration must be greater than 0");
    }

    @Test
    void constructor_shouldThrowException_whenIngredientsIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> new Recipe(
                VALID_ID,
                new RecipeDetails(
                        VALID_NAME,
                        VALID_DESCRIPTION,
                        VALID_DURATION,
                        VALID_SERVINGS,
                        VALID_STEPS,
                        null
                ),
                USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A recipe must have at least one ingredient");
    }
}
