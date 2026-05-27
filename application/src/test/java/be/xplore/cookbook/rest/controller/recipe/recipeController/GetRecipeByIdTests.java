package be.xplore.cookbook.rest.controller.recipe.recipeController;

import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetRecipeByIdTests extends BaseIntegrationTest {
    private static final int DURATION_IN_MINUTES = 60;
    @Override
    protected String[] getTablesToClear() {
        return new String[]{"recipe_ingredients", "recipe_steps", "recipes", "ingredients", "users"};
    }

    @Test
    void getRecipeById_shouldReturnRecipe_whenRecipeExists() throws Exception {
        // Arrange
        Ingredient ingredient1 = createAndSaveIngredient("Flour");
        Ingredient ingredient2 = createAndSaveIngredient("Butter");
        User user = createUser();

        Recipe recipe = createAndSaveRecipeWithIngredients(List.of(ingredient1, ingredient2), user);

        // Act & Assert
        performGetRecipeById(recipe.getId().id())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(recipe.getId().id().toString()))
                .andExpect(jsonPath("$.name").value("Test Name"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.durationInMinutes").value(DURATION_IN_MINUTES))
                .andExpect(jsonPath("$.servings").value(2))
                .andExpect(jsonPath("$.steps[0]").value("This is step 1"))
                .andExpect(jsonPath("$.steps[1]").value("This is step 2"))
                .andExpect(jsonPath("$.ingredients[*].ingredientId", hasItems(
                        ingredient1.id().id().toString(),
                        ingredient2.id().id().toString()
                )))
                .andExpect(jsonPath("$.ingredients[*].name", hasItems("Flour", "Butter")))
                .andExpect(jsonPath("$.ingredients[*].quantity", hasItem(1.0)))
                .andExpect(jsonPath("$.ingredients[*].unit", hasItem("GRAM")))
                .andExpect(jsonPath("$.isOwner").value(true));
    }

    @Test
    void getRecipeById_shouldReturnRecipe_whenRecipeExistsAndIsAccessible() throws Exception {
        // Arrange
        User user1 = createUser();
        User user2 = createUserWithId(UserId.create());

        createHouseholdWithMembers(List.of(user2), user1);
        Recipe recipeByOtherUser = createAndSaveRecipe(true, user2); // true = public

        // Act & Assert
        performGetRecipeByIdWithPredefinedUserId(recipeByOtherUser.getId().id(), user1.id())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(recipeByOtherUser.getId().id().toString()))
                .andExpect(jsonPath("$.isOwner").value(false));
    }

    @Test
    void getRecipeById_shouldReturn404_whenRecipeExistsAndIsPrivate() throws Exception {
        // Arrange
        User user1 = createUser();
        User user2 = createUserWithId(UserId.create());

        createHouseholdWithMembers(List.of(user2), user1);
        Recipe recipeByOtherUser = createAndSaveRecipe(false, user2); // false = private

        // Act & Assert
        performGetRecipeByIdWithPredefinedUserId(recipeByOtherUser.getId().id(), user1.id())
                .andExpect(status().isNotFound());
    }

    @Test
    void getRecipeById_shouldReturn404_whenRecipeDoesNotExist() throws Exception {
        // Arrange
        createUser();
        UUID nonExistentRecipeId = UUID.randomUUID();

        // Act & Assert
        performGetRecipeById(nonExistentRecipeId)
                .andExpect(status().isNotFound());
    }

    @Test
    void getRecipeById_shouldReturn404_whenUserNotAllowedToAccess() throws Exception {
        // Arrange
        createUser();
        User otherUser = createUserWithId(UserId.create());

        Ingredient ingredient1 = createAndSaveIngredient("Flour");
        Ingredient ingredient2 = createAndSaveIngredient("Butter");

        Recipe recipe = createAndSaveRecipeWithIngredients(List.of(ingredient1, ingredient2), otherUser);

        // Act & Assert
        performGetRecipeById(recipe.getId().id())
                .andExpect(status().isNotFound());
    }

    @Test
    void getRecipeById_shouldReturn401_whenNotLoggedIn() throws Exception {
        // Arrange
        User user = createUser();
        Ingredient ingredient1 = createAndSaveIngredient("Flour");
        Ingredient ingredient2 = createAndSaveIngredient("Butter");
        Recipe recipe = createAndSaveRecipeWithIngredients(List.of(ingredient1, ingredient2), user);

        // Act & Assert
        getMockMvc().perform(get("/api/recipes/{id}", recipe.getId().id()))
                .andExpect(status().isUnauthorized());
    }

    private ResultActions performGetRecipeById(UUID id) throws Exception {
        return getMockMvc().perform(get("/api/recipes/{id}", id)
                .with(validJwt())
                .with(csrf()));
    }

    private ResultActions performGetRecipeByIdWithPredefinedUserId(UUID id, UserId userId) throws Exception {
        return getMockMvc().perform(get("/api/recipes/{id}", id)
                .with(validJwtFromUserId(userId))
                .with(csrf()));
    }
}
