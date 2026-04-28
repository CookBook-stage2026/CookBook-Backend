package be.xplore.cookbook.rest.controller.recipeController;

import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SearchRecipeSummariesTests extends BaseIntegrationTest {

    private static final int DEFAULT_SIZE = 3;
    private static final int AMOUNT_OF_RECIPES = 15;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int SECONDARY_PAGE_SIZE = 5;
    private static final int MINUTES_IN_HOUR = 60;

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"recipe_ingredients", "recipe_steps", "recipes", "ingredients", "users"};
    }

    @Test
    void searchRecipeSummaries_shouldReturnMatchingRecipes_whenQueryMatches() throws Exception {
        // Arrange
        var user = createUser();
        createAndSaveRecipe(user);
        createAndSaveRecipe(user);
        createAndSaveRecipe(user);

        String query = "Test";

        // Act & Assert
        performSearch(query, 0, DEFAULT_PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(DEFAULT_SIZE)))
                .andExpect(jsonPath("$[0].name").value("Test Name"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].durationInMinutes").value(MINUTES_IN_HOUR));
    }

    @Test
    void searchRecipeSummaries_shouldReturnEmptyList_whenNoRecipesMatchQuery() throws Exception {
        // Arrange
        var user = createUser();
        createAndSaveRecipe(user);
        createAndSaveRecipe(user);

        String query = "NonExistentRecipe";

        // Act & Assert
        performSearch(query, 0, DEFAULT_PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchRecipeSummaries_shouldReturnMultipleRecipes_whenQueryMatchesSeveral() throws Exception {
        // Arrange
        var user = createUser();
        createAndSaveRecipe(user);
        createAndSaveRecipe(user);

        String query = "Test";

        // Act & Assert
        performSearch(query, 0, DEFAULT_PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Test Name"))
                .andExpect(jsonPath("$[1].name").value("Test Name"));
    }

    @Test
    void searchRecipeSummaries_shouldBeCaseInsensitive_whenSearching() throws Exception {
        // Arrange
        var user = createUser();
        createAndSaveRecipe(user);

        String query = "TEST NAME";

        // Act & Assert
        performSearch(query, 0, DEFAULT_PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Name"));
    }

    @Test
    void searchRecipeSummaries_shouldReturnAllRecipes_whenQueryIsEmpty() throws Exception {
        // Arrange
        var user = createUser();
        createAndSaveRecipe(user);
        createAndSaveRecipe(user);
        createAndSaveRecipe(user);

        String query = "";

        // Act & Assert
        performSearch(query, 0, DEFAULT_PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(DEFAULT_SIZE)));
    }

    @Test
    void searchRecipeSummaries_shouldReturnEmptyList_whenNoRecipesExist() throws Exception {
        // Arrange
        String query = "pizza";

        // Act & Assert
        performSearch(query, 0, DEFAULT_PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchRecipeSummaries_shouldHandlePagination_whenMultiplePages() throws Exception {
        // Arrange
        var user = createUser();
        for (int i = 1; i <= AMOUNT_OF_RECIPES; i++) {
            createAndSaveRecipe(user);
        }

        String query = "Test";

        // Act & Assert - First page
        performSearch(query, 0, DEFAULT_PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(DEFAULT_PAGE_SIZE)));

        // Second page
        performSearch(query, 1, DEFAULT_PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(SECONDARY_PAGE_SIZE)));
    }

    @Test
    void searchRecipeSummaries_shouldReturnEmptyPage_whenPageNumberExceedsTotalPages() throws Exception {
        // Arrange
        var user = createUser();
        createAndSaveRecipe(user);

        String query = "Test";

        // Act & Assert
        performSearch(query, SECONDARY_PAGE_SIZE, DEFAULT_PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchRecipeSummaries_shouldMatchExactName_whenQueryMatchesExactly() throws Exception {
        // Arrange
        var user = createUser();
        createAndSaveRecipe(user);
        createAndSaveRecipe(user);

        String query = "Test Name";

        // Act & Assert
        performSearch(query, 0, DEFAULT_PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    private ResultActions performSearch(String query, int page, int size) throws Exception {
        return getMockMvc().perform(get("/api/recipes/search")
                        .with(validJwt())
                        .with(csrf())
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("query", query))
                .andDo(print());
    }
}
