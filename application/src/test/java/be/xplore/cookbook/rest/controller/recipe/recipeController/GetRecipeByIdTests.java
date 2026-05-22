package be.xplore.cookbook.rest.controller.recipe.recipeController;

import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.Unit;
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

    private static final String INGREDIENT_FLOUR_NAME = "Flour";
    private static final String INGREDIENT_BUTTER_NAME = "Butter";
    private static final Unit INGREDIENT_DEFAULT_UNIT = Unit.GRAM;
    private static final double INGREDIENT_DEFAULT_QUANTITY = 1.0;

    private static final String RECIPE_TEST_NAME = "Test Name";
    private static final String RECIPE_TEST_DESCRIPTION = "Test Description";
    private static final int RECIPE_DURATION_MINUTES = 60;
    private static final int RECIPE_SERVINGS = 2;
    private static final String RECIPE_STEP_1 = "This is step 1";
    private static final String RECIPE_STEP_2 = "This is step 2";
    private static final boolean RECIPE_IS_PUBLIC = true;
    private static final boolean RECIPE_IS_PRIVATE = false;

    private static final String JSON_PATH_ID = "$.id";
    private static final String JSON_PATH_NAME = "$.name";
    private static final String JSON_PATH_DESCRIPTION = "$.description";
    private static final String JSON_PATH_DURATION = "$.durationInMinutes";
    private static final String JSON_PATH_SERVINGS = "$.servings";
    private static final String JSON_PATH_STEP_0 = "$.steps[0]";
    private static final String JSON_PATH_STEP_1 = "$.steps[1]";
    private static final String JSON_PATH_INGREDIENT_IDS = "$.ingredients[*].ingredientId";
    private static final String JSON_PATH_INGREDIENT_NAMES = "$.ingredients[*].name";
    private static final String JSON_PATH_INGREDIENT_QUANTITY = "$.ingredients[*].quantity";
    private static final String JSON_PATH_INGREDIENT_UNIT = "$.ingredients[*].unit";
    private static final String JSON_PATH_IS_OWNER = "$.isOwner";

    private static final String API_RECIPE_BY_ID_PATH = "/api/recipes/{id}";
    private static final String MEDIA_TYPE_JSON = "application/json";

    private static final boolean IS_OWNER_TRUE = true;
    private static final boolean IS_OWNER_FALSE = false;

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"recipe_ingredients", "recipe_steps", "recipes", "ingredients", "users"};
    }

    @Test
    void getRecipeById_shouldReturnRecipe_whenRecipeExists() throws Exception {
        // Arrange
        Ingredient ingredient1 = createAndSaveIngredient(INGREDIENT_FLOUR_NAME);
        Ingredient ingredient2 = createAndSaveIngredient(INGREDIENT_BUTTER_NAME);

        User user = createUser();

        Recipe recipe = createAndSaveRecipeWithIngredients(List.of(ingredient1, ingredient2), user);

        // Act & Assert
        performGetRecipeById(recipe.getId().id())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE_JSON))
                .andExpect(jsonPath(JSON_PATH_ID).value(recipe.getId().id().toString()))
                .andExpect(jsonPath(JSON_PATH_NAME).value(RECIPE_TEST_NAME))
                .andExpect(jsonPath(JSON_PATH_DESCRIPTION).value(RECIPE_TEST_DESCRIPTION))
                .andExpect(jsonPath(JSON_PATH_DURATION).value(RECIPE_DURATION_MINUTES))
                .andExpect(jsonPath(JSON_PATH_SERVINGS).value(RECIPE_SERVINGS))
                .andExpect(jsonPath(JSON_PATH_STEP_0).value(RECIPE_STEP_1))
                .andExpect(jsonPath(JSON_PATH_STEP_1).value(RECIPE_STEP_2))
                .andExpect(jsonPath(JSON_PATH_INGREDIENT_IDS, hasItems(
                        ingredient1.id().id().toString(),
                        ingredient2.id().id().toString()
                )))
                .andExpect(jsonPath(JSON_PATH_INGREDIENT_NAMES, hasItems(
                        INGREDIENT_FLOUR_NAME, INGREDIENT_BUTTER_NAME)))
                .andExpect(jsonPath(JSON_PATH_INGREDIENT_QUANTITY, hasItem(INGREDIENT_DEFAULT_QUANTITY)))
                .andExpect(jsonPath(JSON_PATH_INGREDIENT_UNIT, hasItem(INGREDIENT_DEFAULT_UNIT.toString())))
                .andExpect(jsonPath(JSON_PATH_IS_OWNER).value(IS_OWNER_TRUE));
    }

    @Test
    void getRecipeById_shouldReturnRecipe_whenRecipeExistsAndIsAccessible() throws Exception {
        // Arrange
        User user1 = createUser();
        User user2 = createUserWithId(UserId.create());

        createHouseholdWithMembers(List.of(user2), user1);

        Recipe recipeByOtherUser = createAndSaveRecipe(RECIPE_IS_PUBLIC, user2);

        // Act & Assert
        performGetRecipeByIdWithPredefinedUserId(recipeByOtherUser.getId().id(), user1.id())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE_JSON))
                .andExpect(jsonPath(JSON_PATH_ID).value(recipeByOtherUser.getId().id().toString()))
                .andExpect(jsonPath(JSON_PATH_IS_OWNER).value(IS_OWNER_FALSE));
    }

    @Test
    void getRecipeById_shouldReturn404_whenRecipeExistsAndIsPrivate() throws Exception {
        // Arrange
        User user1 = createUser();
        User user2 = createUserWithId(UserId.create());

        createHouseholdWithMembers(List.of(user2), user1);

        Recipe recipeByOtherUser = createAndSaveRecipe(RECIPE_IS_PRIVATE, user2);

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

        Ingredient ingredient1 = createAndSaveIngredient(INGREDIENT_FLOUR_NAME);
        Ingredient ingredient2 = createAndSaveIngredient(INGREDIENT_BUTTER_NAME);

        Recipe recipe = createAndSaveRecipeWithIngredients(List.of(ingredient1, ingredient2), otherUser);

        // Act & Assert
        performGetRecipeById(recipe.getId().id())
                .andExpect(status().isNotFound());
    }

    @Test
    void getRecipeById_shouldReturn401_whenNotLoggedIn() throws Exception {
        // Arrange
        User user = createUser();

        Ingredient ingredient1 = createAndSaveIngredient(INGREDIENT_FLOUR_NAME);
        Ingredient ingredient2 = createAndSaveIngredient(INGREDIENT_BUTTER_NAME);
        Recipe recipe = createAndSaveRecipeWithIngredients(List.of(ingredient1, ingredient2), user);

        // Act & Assert
        getMockMvc().perform(get(API_RECIPE_BY_ID_PATH, recipe.getId().id()))
                .andExpect(status().isUnauthorized());
    }

    private ResultActions performGetRecipeById(UUID id) throws Exception {
        return getMockMvc().perform(get(API_RECIPE_BY_ID_PATH, id)
                .with(validJwt())
                .with(csrf()));
    }

    private ResultActions performGetRecipeByIdWithPredefinedUserId(UUID id, UserId userId) throws Exception {
        return getMockMvc().perform(get(API_RECIPE_BY_ID_PATH, id)
                .with(validJwtFromUserId(userId))
                .with(csrf()));
    }
}
