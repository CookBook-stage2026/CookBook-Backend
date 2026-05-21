package be.xplore.cookbook.rest.controller.recipeController;

import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeleteRecipeTests extends BaseIntegrationTest {

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"recipe_ingredients", "recipe_steps", "recipes", "ingredients", "users"};
    }

    @Test
    void deleteRecipe_shouldReturn204_whenRecipeExistsAndBelongsToUser() throws Exception {
        // Arrange
        User user = createUser();

        Ingredient ingredient = createAndSaveIngredient("Flour");

        Recipe recipe = createAndSaveRecipeWithIngredients(List.of(ingredient), user);

        // Act & Assert
        performDeleteRecipe(recipe.getId().id(), user.id())
                .andExpect(status().isNoContent());

        boolean exists = getRecipeRepository()
                .findById(recipe.getId(), user)
                .isPresent();

        assertThat(exists).isFalse();
    }

    @Test
    void deleteRecipe_shouldReturn404_whenRecipeDoesNotExist() throws Exception {
        // Arrange
        User user = createUser();

        // Act & Assert
        performDeleteRecipe(UUID.randomUUID(), user.id())
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRecipe_shouldReturn404_whenRecipeBelongsToAnotherUser() throws Exception {
        // Arrange
        User owner = createUser();
        User otherUser = createUserWithId(UserId.create());

        Ingredient ingredient = createAndSaveIngredient("Flour");

        Recipe recipe = createAndSaveRecipeWithIngredients(List.of(ingredient), owner);

        // Act & Assert
        performDeleteRecipe(recipe.getId().id(), otherUser.id())
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRecipe_shouldReturn401_whenNotAuthenticated() throws Exception {
        // Arrange
        User user = createUser();

        Ingredient ingredient = createAndSaveIngredient("Flour");

        Recipe recipe = createAndSaveRecipeWithIngredients(List.of(ingredient), user);

        // Act & Assert
        getMockMvc().perform(delete("/api/recipes/{id}", recipe.getId().id())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    private ResultActions performDeleteRecipe(UUID recipeId, UserId userId) throws Exception {
        return getMockMvc().perform(delete("/api/recipes/{id}", recipeId)
                .with(validJwtFromUserId(userId))
                .with(csrf()));
    }
}
