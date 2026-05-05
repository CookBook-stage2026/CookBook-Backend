package be.xplore.cookbook.rest.controller.recipeController;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.request.RecipeSearchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FilterRecipesTests extends BaseIntegrationTest {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int NUMBER_OF_RECIPES = 3;

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"recipe_ingredients", "recipe_steps", "recipes", "ingredients", "users"};
    }

    @Test
    void filterRecipes_shouldReturnAllRecipes_whenRecipesExist() throws Exception {
        // Arrange
        User user = createUser();
        Recipe recipe1 = createAndSaveRecipe(user);
        Recipe recipe2 = createAndSaveRecipe(user);

        // Act & Assert
        performFilter(defaultRequest())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].id", hasItems(
                        recipe1.getId().id().toString(),
                        recipe2.getId().id().toString()
                )))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    void filterRecipes_shouldReturnEmptyList_whenNoRecipesExist() throws Exception {
        // Arrange
        createUser();

        // Act & Assert
        performFilter(defaultRequest())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.page.totalPages").value(0));
    }

    @Test
    void filterRecipes_shouldReturnPagedResults_whenPageSizeIsSmall() throws Exception {
        // Arrange
        User user = createUser();

        final int totalRecipes = 3;
        final int pageSize = 2;
        final int firstPageIndex = 0;
        final int secondPageIndex = 1;

        final int expectedTotalPages = (int) Math.ceil((double) totalRecipes / pageSize);
        final int expectedFirstPageCount = Math.min(totalRecipes, pageSize);
        final int expectedSecondPageCount = totalRecipes - pageSize;

        for (int i = 0; i < totalRecipes; i++) {
            createAndSaveRecipe(user);
        }

        long totalElements = getRecipeRepository().count();

        // Act & Assert
        performFilter(new RecipeSearchRequest(List.of(), true, firstPageIndex, pageSize))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(expectedFirstPageCount)))
                .andExpect(jsonPath("$.page.totalElements").value(totalElements))
                .andExpect(jsonPath("$.page.totalPages").value(expectedTotalPages))
                .andExpect(jsonPath("$.page.number").value(firstPageIndex))
                .andExpect(jsonPath("$.page.size").value(pageSize));

        performFilter(new RecipeSearchRequest(List.of(), true, secondPageIndex, pageSize))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(expectedSecondPageCount)))
                .andExpect(jsonPath("$.page.number").value(secondPageIndex))
                .andExpect(jsonPath("$.page.size").value(pageSize));
    }

    @Test
    void filterRecipes_shouldReturn401_whenNotAuthenticated() throws Exception {
        // Arrange
        User user = createUser();
        createAndSaveRecipe(user);

        // Act & Assert
        getMockMvc().perform(post("/api/recipes/filter")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(defaultRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void filterRecipes_shouldReturnEmptyList_whenNoRecipesOfLoggedInUserExist() throws Exception {
        // Arrange
        var user1 = createUser();
        var user2 = createUserWithId(UserId.create());
        createAndSaveRecipe(user1);

        // Act & Assert
        performFilterWithPredefinedUserId(defaultRequest(), user2.id())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.page.totalPages").value(0));
    }

    @Test
    void filterRecipes_shouldFilterByIngredients_whenIngredientIdsProvided() throws Exception {
        // Arrange
        Ingredient flour = createAndSaveIngredient("Flour", Unit.GRAM, Category.GRAIN);
        Ingredient sugar = createAndSaveIngredient("Sugar", Unit.GRAM, Category.GRAIN);
        Ingredient salt = createAndSaveIngredient("Salt", Unit.GRAM, Category.GRAIN);

        User user = createUser();

        Recipe recipe1 = createAndSaveRecipeWithIngredients(List.of(flour, sugar), user);
        Recipe recipe2 = createAndSaveRecipeWithIngredients(List.of(flour, sugar, salt), user);
        createAndSaveRecipeWithIngredients(List.of(flour), user);
        createAndSaveRecipeWithIngredients(List.of(salt), user);

        RecipeSearchRequest dto = new RecipeSearchRequest(List.of(flour.id().id(), sugar.id().id()),
                true, DEFAULT_PAGE, DEFAULT_PAGE_SIZE);

        // Act & Assert
        performFilter(dto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].id", hasItems(
                        recipe1.getId().id().toString(),
                        recipe2.getId().id().toString()
                )))
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    void filterRecipes_shouldReturnEmptyResults_whenNoRecipesMatchIngredientFilter() throws Exception {
        // Arrange
        Ingredient flour = createAndSaveIngredient("Flour", Unit.GRAM, Category.GRAIN);
        User user = createUser();
        createAndSaveRecipeWithIngredients(List.of(flour), user);

        // Act & Assert
        performFilter(new RecipeSearchRequest(List.of(UUID.randomUUID()), true, DEFAULT_PAGE, DEFAULT_PAGE_SIZE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.page.totalElements").value(0));
    }

    @Test
    void filterRecipes_shouldExcludeRecipesConflictingWithPreferences_whenPreferencesAreSet() throws Exception {
        // Arrange
        Ingredient flour = createAndSaveIngredient("Flour", Unit.GRAM, Category.GRAIN);
        Ingredient sugar = createAndSaveIngredient("Sugar", Unit.GRAM, Category.GRAIN);
        Ingredient milk = createAndSaveIngredient("Milk", Unit.LITER, Category.DAIRY);

        User user = createUser();

        getUserPreferenceRepository().save(new UserPreferences(
                user,
                List.of(Category.DAIRY),
                List.of(flour)
        ));

        createAndSaveRecipeWithIngredients(List.of(flour), user);
        createAndSaveRecipeWithIngredients(List.of(milk), user);
        Recipe expected = createAndSaveRecipeWithIngredients(List.of(sugar), user);

        // Act & Assert
        performFilter(defaultRequest())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(expected.getId().id().toString()))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void filterRecipes_shouldReturnRecipesFromHouseholdMembers_whenUserIsInHousehold() throws Exception {
        // Arrange
        User user1 = createUser();
        User user2 = createUserWithId(UserId.create());
        User user3 = createUserWithId(UserId.create());

        List<User> householdMembers = new ArrayList<>();
        householdMembers.add(user2);
        householdMembers.add(user3);

        createHouseholdWithMembers(householdMembers, user1);

        Recipe recipeByUser1 = createAndSaveRecipe(user1);
        Recipe recipeByUser2 = createAndSaveRecipe(user2);
        Recipe recipeByUser3 = createAndSaveRecipe(user3);

        // Act & Assert
        performFilterWithPredefinedUserId(defaultRequest(), user1.id())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(NUMBER_OF_RECIPES)))
                .andExpect(jsonPath("$.content[*].id", hasItems(
                        recipeByUser1.getId().id().toString(),
                        recipeByUser2.getId().id().toString(),
                        recipeByUser3.getId().id().toString()
                )))
                .andExpect(jsonPath("$.page.totalElements").value(NUMBER_OF_RECIPES));
    }

    @Test
    void filterRecipes_shouldNotReturnRecipesFromNonHouseholdMembers_whenUserIsInHousehold() throws Exception {
        // Arrange
        User user1 = createUser();
        User user2 = createUserWithId(UserId.create());
        User userOutsideHousehold = createUserWithId(UserId.create());

        List<User> householdMembers = new ArrayList<>();
        householdMembers.add(user2);

        createHouseholdWithMembers(householdMembers, user1);

        Recipe recipeByUser1 = createAndSaveRecipe(user1);
        createAndSaveRecipe(userOutsideHousehold);

        // Act & Assert
        performFilterWithPredefinedUserId(defaultRequest(), user1.id())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(recipeByUser1.getId().id().toString()))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void filterRecipes_shouldReturnOnlyOwnRecipes_whenUserIsNotInAnyHousehold() throws Exception {
        // Arrange
        User user1 = createUser();
        User user2 = createUserWithId(UserId.create());

        Recipe recipeByUser1 = createAndSaveRecipe(user1);
        createAndSaveRecipe(user2);

        // Act & Assert
        performFilterWithPredefinedUserId(defaultRequest(), user1.id())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(recipeByUser1.getId().id().toString()))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void filterRecipes_shouldApplyIngredientFilterAcrossHouseholdMembers() throws Exception {
        // Arrange
        Ingredient flour = createAndSaveIngredient("Flour", Unit.GRAM, Category.GRAIN);
        Ingredient sugar = createAndSaveIngredient("Sugar", Unit.GRAM, Category.GRAIN);

        User user1 = createUser();
        User user2 = createUserWithId(UserId.create());


        List<User> householdMembers = new ArrayList<>();
        householdMembers.add(user2);

        createHouseholdWithMembers(householdMembers, user1);

        Recipe recipeByUser1 = createAndSaveRecipeWithIngredients(List.of(flour), user1);
        createAndSaveRecipeWithIngredients(List.of(sugar), user2);

        RecipeSearchRequest dto = new RecipeSearchRequest(
                List.of(flour.id().id()),
                true, DEFAULT_PAGE, DEFAULT_PAGE_SIZE
        );

        // Act & Assert
        performFilterWithPredefinedUserId(dto, user1.id())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(recipeByUser1.getId().id().toString()))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void filterRecipes_shouldApplyExclusionPreferencesAcrossHouseholdMembers() throws Exception {
        // Arrange
        Ingredient flour = createAndSaveIngredient("Flour", Unit.GRAM, Category.GRAIN);
        Ingredient sugar = createAndSaveIngredient("Sugar", Unit.GRAM, Category.GRAIN);

        User user1 = createUser();
        User user2 = createUserWithId(UserId.create());

        List<User> householdMembers = new ArrayList<>();
        householdMembers.add(user2);

        createHouseholdWithMembers(householdMembers, user1);

        getUserPreferenceRepository().save(new UserPreferences(
                user1,
                List.of(),
                List.of(flour)
        ));

        createAndSaveRecipeWithIngredients(List.of(flour), user1);
        createAndSaveRecipeWithIngredients(List.of(flour), user2);
        Recipe nonExcludedRecipe = createAndSaveRecipeWithIngredients(List.of(sugar), user2);

        // Act & Assert
        performFilterWithPredefinedUserId(defaultRequest(), user1.id())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(nonExcludedRecipe.getId().id().toString()))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    private RecipeSearchRequest defaultRequest() {
        return new RecipeSearchRequest(List.of(), true, DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
    }

    private ResultActions performFilter(RecipeSearchRequest request) throws Exception {
        return getMockMvc().perform(post("/api/recipes/filter")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(request)))
                .andDo(print());
    }

    private ResultActions performFilterWithPredefinedUserId(RecipeSearchRequest request,
                                                            UserId userId) throws Exception {
        return getMockMvc().perform(post("/api/recipes/filter")
                        .with(validJwtFromUserId(userId))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(request)))
                .andDo(print());
    }
}
