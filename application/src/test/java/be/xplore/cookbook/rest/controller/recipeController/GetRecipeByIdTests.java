package be.xplore.cookbook.rest.controller.recipeController;

import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetRecipeByIdTests extends BaseIntegrationTest {

    private static final String DEFAULT_INGREDIENT_NAME = "Flour";
    private static final String DEFAULT_INGREDIENT_NAME_2 = "Butter";
    private static final Unit DEFAULT_UNIT = Unit.GRAM;
    private static final int MINUTES_IN_HOUR = 60;

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"recipe_ingredients", "recipe_steps", "recipes", "ingredients", "users"};
    }

    @Test
    void getRecipeById_shouldReturnRecipe_whenRecipeExists() throws Exception {
        // Arrange
        Ingredient ingredient1 = createAndSaveIngredient(DEFAULT_INGREDIENT_NAME);
        Ingredient ingredient2 = createAndSaveIngredient(DEFAULT_INGREDIENT_NAME_2);

        User user = createUser();

        Recipe recipe = createAndSaveRecipeWithIngredients(List.of(ingredient1, ingredient2), user);

        // Act & Assert
        performGetRecipeById(recipe.getId().id())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(recipe.getId().id().toString()))
                .andExpect(jsonPath("$.name").value("Test Name"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.durationInMinutes").value(MINUTES_IN_HOUR))
                .andExpect(jsonPath("$.servings").value(2))
                .andExpect(jsonPath("$.steps[0]").value("This is step 1"))
                .andExpect(jsonPath("$.steps[1]").value("This is step 2"))
                .andExpect(jsonPath("$.ingredients[*].ingredientId", hasItems(
                        ingredient1.id().id().toString(),
                        ingredient2.id().id().toString()
                )))
                .andExpect(jsonPath("$.ingredients[*].name", hasItems(
                        DEFAULT_INGREDIENT_NAME, DEFAULT_INGREDIENT_NAME_2)))
                .andExpect(jsonPath("$.ingredients[*].baseQuantity", hasItem(1.0)))
                .andExpect(jsonPath("$.ingredients[*].unit", hasItem(DEFAULT_UNIT.toString())));
    }

    @Test
    void getRecipeById_shouldReturn404_whenRecipeDoesNotExist() throws Exception {
        // Arrange
        createUser();

        // Act & Assert
        performGetRecipeById(UUID.randomUUID())
                .andExpect(status().isNotFound());
    }

    @Test
    void getRecipeById_shouldReturn404_whenUserNotAllowedToAccess() throws Exception {
        // Arrange
        createUser();
        User otherUser = new User("email", "name", List.of());

        Ingredient ingredient1 = createAndSaveIngredient(DEFAULT_INGREDIENT_NAME);
        Ingredient ingredient2 = createAndSaveIngredient(DEFAULT_INGREDIENT_NAME_2);

        Recipe recipe = createAndSaveRecipeWithIngredients(List.of(ingredient1, ingredient2), otherUser);

        // Act & Assert
        performGetRecipeById(recipe.getId().id())
                .andExpect(status().isNotFound());
    }

    @Test
    void getRecipeById_shouldReturn401_whenNotLoggedIn() throws Exception {
        // Arrange
        User user = createUser();

        Ingredient ingredient1 = createAndSaveIngredient(DEFAULT_INGREDIENT_NAME);
        Ingredient ingredient2 = createAndSaveIngredient(DEFAULT_INGREDIENT_NAME_2);
        Recipe recipe = createAndSaveRecipeWithIngredients(List.of(ingredient1, ingredient2), user);

        // Act & Assert
        getMockMvc().perform(get("/api/recipes/{id}", recipe.getId().id()))
                .andExpect(status().isUnauthorized());
    }

    private ResultActions performGetRecipeById(UUID id) throws Exception {
        return getMockMvc().perform(get("/api/recipes/{id}", id)
                        .with(validJwt())
                        .with(csrf()))
                .andDo(print());
    }
}
