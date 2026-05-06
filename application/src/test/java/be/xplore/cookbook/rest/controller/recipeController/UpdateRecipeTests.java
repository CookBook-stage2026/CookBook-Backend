package be.xplore.cookbook.rest.controller.recipeController;

import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.request.NewRecipeIngredientDto;
import be.xplore.cookbook.rest.dto.request.UpdateRecipeDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdateRecipeTests extends BaseIntegrationTest {

    private static final double DEFAULT_QUANTITY = 1.0;
    private static final int MINUTES_IN_HOUR = 60;
    private static final String UPDATED_NAME = "Updated Name";
    private static final String UPDATED_DESCRIPTION = "Updated Description";
    private static final String UPDATED_STEP_1 = "Updated step 1";
    private static final String UPDATED_STEP_2 = "Updated step 2";
    private static final int UPDATED_SERVINGS = 4;

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"recipe_ingredients", "recipe_steps", "recipes", "ingredients", "users"};
    }

    @Test
    void updateRecipe_shouldReturn204_whenRequestIsValid() throws Exception {
        // Arrange
        User user = createUser();
        Ingredient flour = createAndSaveIngredient("Flour");
        Recipe recipe = createAndSaveRecipe(user);

        UpdateRecipeDto dto = buildUpdateRecipeDto(List.of(
                new NewRecipeIngredientDto(flour.id().id(), DEFAULT_QUANTITY)
        ));

        // Act & Assert
        performUpdateRecipe(recipe.id().id().toString(), dto)
                .andExpect(status().isNoContent());

        Recipe updatedRecipe = getRecipeRepository()
                .findById(recipe.id(), user.id())
                .orElseThrow();

        assertThat(updatedRecipe.name()).isEqualTo(UPDATED_NAME);
        assertThat(updatedRecipe.description()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(updatedRecipe.durationInMinutes()).isEqualTo(MINUTES_IN_HOUR);
        assertThat(updatedRecipe.steps()).containsExactly(UPDATED_STEP_1, UPDATED_STEP_2);
        assertThat(updatedRecipe.servings()).isEqualTo(UPDATED_SERVINGS);
        assertThat(updatedRecipe.ingredients()).hasSize(1);
    }

    @Test
    void updateRecipe_shouldReturn400_whenIngredientDoesNotExist() throws Exception {
        // Arrange
        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);

        UpdateRecipeDto dto = buildUpdateRecipeDto(List.of(
                new NewRecipeIngredientDto(UUID.randomUUID(), DEFAULT_QUANTITY)
        ));

        // Act & Assert
        performUpdateRecipe(recipe.id().id().toString(), dto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRecipe_shouldReturn404_whenRecipeNotFound() throws Exception {
        // Arrange
        createUser();
        Ingredient ingredient = createAndSaveIngredient("Flour");

        UpdateRecipeDto dto = buildUpdateRecipeDto(List.of(
                new NewRecipeIngredientDto(ingredient.id().id(), DEFAULT_QUANTITY)
        ));

        // Act & Assert
        performUpdateRecipe(UUID.randomUUID().toString(), dto)
                .andExpect(status().isNotFound());
    }

    @Test
    void updateRecipe_shouldReturn404_whenRecipeBelongsToOtherUser() throws Exception {
        // Arrange
        User owner = createUserWithId(UserId.create());
        Recipe recipe = createAndSaveRecipe(owner);
        Ingredient flour = createAndSaveIngredient("Flour");

        createUser();

        UpdateRecipeDto dto = buildUpdateRecipeDto(List.of(
                new NewRecipeIngredientDto(flour.id().id(), DEFAULT_QUANTITY)
        ));

        // Act & Assert
        performUpdateRecipe(recipe.id().id().toString(), dto)
                .andExpect(status().isNotFound());
    }

    @Test
    void updateRecipe_shouldReturn401_whenNotAuthenticated() throws Exception {
        // Arrange
        Ingredient flour = createAndSaveIngredient("Flour");

        UpdateRecipeDto dto = buildUpdateRecipeDto(List.of(
                new NewRecipeIngredientDto(flour.id().id(), DEFAULT_QUANTITY)
        ));

        // Act & Assert
        getMockMvc().perform(put("/api/recipes/{id}", UUID.randomUUID())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    private UpdateRecipeDto buildUpdateRecipeDto(List<NewRecipeIngredientDto> ingredients) {
        return new UpdateRecipeDto(
                UPDATED_NAME,
                UPDATED_DESCRIPTION,
                MINUTES_IN_HOUR,
                List.of(UPDATED_STEP_1, UPDATED_STEP_2),
                ingredients,
                UPDATED_SERVINGS
        );
    }

    private ResultActions performUpdateRecipe(String recipeId, UpdateRecipeDto dto) throws Exception {
        return getMockMvc().perform(put("/api/recipes/{id}", recipeId)
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andDo(print());
    }
}
