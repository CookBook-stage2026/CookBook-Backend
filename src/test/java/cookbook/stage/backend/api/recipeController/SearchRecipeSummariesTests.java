package cookbook.stage.backend.api.recipeController;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.ingredient.IngredientRepository;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeDetails;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeIngredient;
import be.xplore.cookbook.core.domain.recipe.RecipeRepository;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class SearchRecipeSummariesTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    private static final UserId USER_ID = UserId.create();
    private static final String USER_NAME = "username";
    private static final String USER_EMAIL = "user@email.com";

    private static final int DEFAULT_DURATION_IN_MINUTES = 15;
    private static final int PAGE_SIZE = 10;
    private static final String DEFAULT_RECIPE_DESCRIPTION = "test";
    private static final int DEFAULT_QUANTITY = 5;
    private static final int DEFAULT_SIZE = 3;
    private static final int AMOUNT_OF_RECIPES = 15;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int SECONDARY_PAGE_SIZE = 5;

    @BeforeEach
    void clearDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "recipe_ingredients", "recipe_steps", "recipes", "ingredients", "users");
    }

    @Test
    void searchRecipes_shouldReturnMatchingRecipes_whenQueryMatches() throws Exception {
        // Arrange
        var user = createUser();
        seedRecipe("Pizza", user.getId());
        seedRecipe("Chocolate cake", user.getId());
        seedRecipe("Ice cream", user.getId());

        String query = "p";

        // Act & Assert
        performSearch(query, 0, PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Pizza"))
                .andExpect(jsonPath("$[0].description").value(DEFAULT_RECIPE_DESCRIPTION))
                .andExpect(jsonPath("$[0].durationInMinutes").value(DEFAULT_DURATION_IN_MINUTES));
    }

    @Test
    void searchRecipes_shouldReturnEmptyList_whenNoRecipesMatchQuery() throws Exception {
        // Arrange
        var user = createUser();
        seedRecipe("Pizza", user.getId());
        seedRecipe("Chocolate cake", user.getId());

        String query = "y";

        // Act & Assert
        performSearch(query, 0, PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchRecipes_shouldReturnMultipleRecipes_whenQueryMatchesSeveral() throws Exception {
        // Arrange
        var user = createUser();
        seedRecipe("Pizza", user.getId());
        seedRecipe("Pasta", user.getId());
        seedRecipe("Chocolate cake", user.getId());

        String query = "p";

        // Act & Assert
        performSearch(query, 0, PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Pasta"))
                .andExpect(jsonPath("$[1].name").value("Pizza"));
    }

    @Test
    void searchRecipes_shouldBeCaseInsensitive_whenSearching() throws Exception {
        // Arrange
        var user = createUser();
        seedRecipe("Pizza", user.getId());
        seedRecipe("Chocolate cake", user.getId());

        String query = "PIZZA";

        // Act & Assert
        performSearch(query, 0, PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Pizza"));
    }

    @Test
    void searchRecipes_shouldReturnAllRecipes_whenQueryIsEmpty() throws Exception {
        // Arrange
        var user = createUser();
        seedRecipe("Pizza", user.getId());
        seedRecipe("Chocolate cake", user.getId());
        seedRecipe("Ice cream", user.getId());

        String query = "";

        // Act & Assert
        performSearch(query, 0, PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(DEFAULT_SIZE)));
    }

    @Test
    void searchRecipes_shouldReturnEmptyList_whenNoRecipesExist() throws Exception {
        // Arrange
        String query = "pizza";

        // Act & Assert
        performSearch(query, 0, PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchRecipes_shouldHandlePagination_whenMultiplePages() throws Exception {
        // Arrange
        var user = createUser();
        for (int i = 1; i <= AMOUNT_OF_RECIPES; i++) {
            seedRecipe("Recipe " + i, user.getId());
        }

        String query = "Recipe";

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
    void searchRecipes_shouldReturnEmptyPage_whenPageNumberExceedsTotalPages() throws Exception {
        // Arrange
        var user = createUser();
        seedRecipe("Pizza", user.getId());

        String query = "pizza";

        // Act & Assert
        performSearch(query, SECONDARY_PAGE_SIZE, PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchRecipes_shouldHandleSpecialCharactersInQuery() throws Exception {
        // Arrange
        var user = createUser();
        seedRecipe("Pizza & Pasta", user.getId());
        seedRecipe("Chocolate cake", user.getId());

        String query = "&";

        // Act & Assert
        performSearch(query, 0, PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Pizza & Pasta"));
    }

    @Test
    void searchRecipes_shouldMatchExactName_whenQueryMatchesExactly() throws Exception {
        // Arrange
        var user = createUser();
        seedRecipe("Pizza", user.getId());
        seedRecipe("Pizza Margherita", user.getId());

        String query = "Pizza";

        // Act & Assert
        performSearch(query, 0, PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    private ResultActions performSearch(String query, int page, int size) throws Exception {
        return mockMvc.perform(get("/api/recipes/search")
                        .with(validJwt())
                        .with(csrf())
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("query", query))
                .andDo(print());
    }

    private User createUser() {
        return userRepository.save(new User(USER_ID, USER_NAME, USER_EMAIL, List.of()));
    }

    private void seedRecipe(String name, UserId userId) {
        var steps = new ArrayList<String>();
        steps.add("step 1");
        steps.add("step 2");

        var ingredients = new ArrayList<Ingredient>();
        ingredients.add(createAndSaveIngredient("flour"));
        List<RecipeIngredient> recipeIngredients = ingredients.stream()
                .map(ing -> new RecipeIngredient(ing, DEFAULT_QUANTITY))
                .toList();

        Recipe recipe = new Recipe(new RecipeId(UUID.randomUUID()), new RecipeDetails(name, DEFAULT_RECIPE_DESCRIPTION,
                DEFAULT_DURATION_IN_MINUTES,
                2, steps), recipeIngredients, userId);
        recipeRepository.save(recipe);
    }

    private Ingredient createAndSaveIngredient(String name) {
        Ingredient ingredient = new Ingredient(new IngredientId(UUID.randomUUID()), name, Unit.GRAM,
                List.of(Category.ADDITIVE));
        return ingredientRepository.save(ingredient);
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject(USER_ID.id().toString())
                .claim("email", USER_EMAIL)
                .claim("name", USER_NAME));
    }
}
