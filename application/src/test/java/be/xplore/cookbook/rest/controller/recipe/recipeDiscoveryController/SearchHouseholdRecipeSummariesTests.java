package be.xplore.cookbook.rest.controller.recipe.recipeDiscoveryController;

import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class SearchHouseholdRecipeSummariesTests extends BaseIntegrationTest {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int AMOUNT_OF_RECIPES = 15;
    private static final int FIRST_PAGE_SIZE = 10;
    private static final int SECOND_PAGE_SIZE = 5;
    private static final String RECIPE_NAME = "Test Recipe";

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"recipe_ingredients", "recipe_steps", "recipes", "ingredients", "users", "households",
                "households_members"};
    }

    @Test
    void searchHouseholdRecipeSummaries_shouldReturnPublicRecipesOfMembers_whenQueryMatches() throws Exception {
        User creator = createUser();
        User member1 = createUserWithId(UserId.create());
        User member2 = createUserWithId(UserId.create());
        Household household = createHouseholdWithMembers(List.of(member1, member2), creator);

        List<Recipe> recipes = List.of(
                createAndSaveRecipe(RECIPE_NAME, true, creator),
                createAndSaveRecipe(RECIPE_NAME, true, member1),
                createAndSaveRecipe(RECIPE_NAME, true, member2)
        );

        performSearch(RECIPE_NAME, DEFAULT_PAGE, DEFAULT_PAGE_SIZE, member1.id(), household)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(recipes.size())));
    }

    @Test
    void searchHouseholdRecipeSummaries_shouldOnlyReturnAccessibleRecipesOfMembers_whenQuerying() throws Exception {
        User creator = createUser();
        User member1 = createUserWithId(UserId.create());
        User member2 = createUserWithId(UserId.create());
        Household household = createHouseholdWithMembers(List.of(member1, member2), creator);

        createAndSaveRecipe(RECIPE_NAME, false, creator);
        Recipe recipe1 = createAndSaveRecipe(RECIPE_NAME, true, creator);
        createAndSaveRecipe(RECIPE_NAME, false, member1);
        Recipe recipe2 = createAndSaveRecipe(RECIPE_NAME, true, member1);
        createAndSaveRecipe(RECIPE_NAME, false, member2);

        performSearch(RECIPE_NAME, DEFAULT_PAGE, DEFAULT_PAGE_SIZE, member1.id(), household)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", contains(
                        recipe1.getId().id().toString(),
                        recipe2.getId().id().toString()
                )));
    }

    @Test
    void searchHouseholdRecipeSummaries_shouldReturnEmptyList_whenNoRecipesMatchQuery() throws Exception {
        var creator = createUser();
        var member = createUserWithId(UserId.create());
        var household = createHouseholdWithMembers(List.of(member), creator);

        createAndSaveRecipe(RECIPE_NAME, true, creator);
        createAndSaveRecipe(RECIPE_NAME, true, member);

        performSearch("NonExistent", DEFAULT_PAGE, DEFAULT_PAGE_SIZE, creator.id(), household)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchHouseholdRecipeSummaries_shouldBeCaseInsensitive_whenSearching() throws Exception {
        var creator = createUser();
        var household = createHouseholdWithMembers(List.of(), creator);

        createAndSaveRecipe(RECIPE_NAME, true, creator);

        performSearch("TEST RECIPE", DEFAULT_PAGE, DEFAULT_PAGE_SIZE, creator.id(), household)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(RECIPE_NAME));
    }

    @Test
    void searchHouseholdRecipeSummaries_shouldReturnAllPublicRecipes_whenQueryIsEmpty() throws Exception {
        var creator = createUser();
        var member = createUserWithId(UserId.create());
        var household = createHouseholdWithMembers(List.of(member), creator);

        createAndSaveRecipe(RECIPE_NAME, true, creator);
        createAndSaveRecipe(RECIPE_NAME, true, member);
        createAndSaveRecipe(RECIPE_NAME, false, member);

        performSearch("", DEFAULT_PAGE, DEFAULT_PAGE_SIZE, creator.id(), household)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void searchHouseholdRecipeSummaries_shouldHandlePagination_whenMultiplePages() throws Exception {
        var creator = createUser();
        var household = createHouseholdWithMembers(List.of(), creator);

        for (int i = 0; i < AMOUNT_OF_RECIPES; i++) {
            createAndSaveRecipe(RECIPE_NAME, true, creator);
        }

        performSearch(RECIPE_NAME, DEFAULT_PAGE, DEFAULT_PAGE_SIZE, creator.id(), household)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(FIRST_PAGE_SIZE)));

        performSearch(RECIPE_NAME, 1, DEFAULT_PAGE_SIZE, creator.id(), household)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(SECOND_PAGE_SIZE)));
    }

    @Test
    void searchHouseholdRecipeSummaries_shouldNotReturnRecipesFromNonMembers_whenQuerying() throws Exception {
        var creator = createUser();
        var nonMember = createUserWithId(UserId.create());
        var household = createHouseholdWithMembers(List.of(), creator);

        createAndSaveRecipe(RECIPE_NAME, true, creator);
        createAndSaveRecipe(RECIPE_NAME, true, nonMember);

        performSearch(RECIPE_NAME, DEFAULT_PAGE, DEFAULT_PAGE_SIZE, creator.id(), household)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchHouseholdRecipeSummaries_shouldReturn401_whenNotAuthenticated() throws Exception {
        var creator = createUser();
        var household = createHouseholdWithMembers(List.of(), creator);

        getMockMvc().perform(get("/api/recipes/search/households/{householdId}", household.id().id())
                        .with(csrf())
                        .param("page", String.valueOf(DEFAULT_PAGE))
                        .param("size", String.valueOf(DEFAULT_PAGE_SIZE))
                        .param("query", RECIPE_NAME))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void searchHouseholdRecipeSummaries_shouldReturn403_whenUserIsNotMemberOfHousehold() throws Exception {
        var creator = createUser();
        var nonMember = createUserWithId(UserId.create());
        var household = createHouseholdWithMembers(List.of(), creator);

        performSearch(RECIPE_NAME, DEFAULT_PAGE, DEFAULT_PAGE_SIZE, nonMember.id(), household)
                .andExpect(status().isForbidden());
    }

    private ResultActions performSearch(String query, int page, int size, UserId userId, Household household)
            throws Exception {
        return getMockMvc().perform(get("/api/recipes/search/households/{householdId}", household.id().id())
                        .with(validJwtFromUserId(userId))
                        .with(csrf())
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("query", query))
                .andDo(print());
    }
}
