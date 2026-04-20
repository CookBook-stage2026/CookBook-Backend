package cookbook.stage.backend.recipe.infrastructure.jpa;

import cookbook.stage.backend.recipe.domain.ingredient.Ingredient;
import cookbook.stage.backend.recipe.domain.ingredient.IngredientId;
import cookbook.stage.backend.recipe.domain.ingredient.Unit;
import cookbook.stage.backend.recipe.domain.recipe.RecipeIngredient;
import cookbook.stage.backend.recipe.infrastructure.jpa.ingredient.JpaIngredientEntity;
import cookbook.stage.backend.recipe.infrastructure.jpa.recipe.JpaRecipeEntity;
import cookbook.stage.backend.recipe.infrastructure.jpa.recipe.JpaRecipeIngredientEntity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JpaRecipeIngredientEntityTest {

    private static final UUID RECIPE_ID = UUID.randomUUID();
    private static final Ingredient INGREDIENT = new Ingredient(IngredientId.create(), "Ingredient", Unit.GRAM);
    private static final double QUANTITY = 100.0;
    private static final int DURATION_TO_COOK_RECIPE = 30;

    @Test
    void fromDomain_shouldCreateEntity() {
        // Arrange
        JpaRecipeEntity recipe = createJpaRecipeEntity();
        RecipeIngredient ri = new RecipeIngredient(INGREDIENT, QUANTITY);
        JpaIngredientEntity ingredient = JpaIngredientEntity.fromDomain(INGREDIENT);

        // Act
        JpaRecipeIngredientEntity entity = new JpaRecipeIngredientEntity(recipe, ri, ingredient);

        // Assert
        assertThat(entity.toDomain()).isEqualTo(ri);
    }

    @Test
    void toDomain_shouldReturnRecipeIngredient() {
        // Arrange
        JpaRecipeEntity recipe = createJpaRecipeEntity();
        RecipeIngredient ri = new RecipeIngredient(INGREDIENT, QUANTITY);
        JpaIngredientEntity ingredient = JpaIngredientEntity.fromDomain(INGREDIENT);
        JpaRecipeIngredientEntity entity = new JpaRecipeIngredientEntity(recipe, ri, ingredient);

        // Act
        RecipeIngredient result = entity.toDomain();

        // Assert
        assertThat(result).isEqualTo(ri);
    }

    private JpaRecipeEntity createJpaRecipeEntity() {
        return new JpaRecipeEntity(RECIPE_ID, "Name", "Desc", DURATION_TO_COOK_RECIPE, List.of("Step"), 2);
    }
}
