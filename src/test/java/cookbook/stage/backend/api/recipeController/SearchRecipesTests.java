package cookbook.stage.backend.api.recipeController;

import cookbook.stage.backend.api.input.RecipeSearchRequest;
import cookbook.stage.backend.domain.ingredient.Category;
import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.ingredient.IngredientRepository;
import cookbook.stage.backend.domain.ingredient.Unit;
import cookbook.stage.backend.domain.recipe.Recipe;
import cookbook.stage.backend.domain.recipe.RecipeDetails;
import cookbook.stage.backend.domain.recipe.RecipeId;
import cookbook.stage.backend.domain.recipe.RecipeIngredient;
import cookbook.stage.backend.domain.recipe.RecipeRepository;
import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.user.UserPreferenceRepository;
import cookbook.stage.backend.domain.user.UserPreferences;
import cookbook.stage.backend.domain.user.UserRepository;
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
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class SearchRecipesTests {

    private static final String DEFAULT_RECIPE_NAME = "Test Name";
    private static final String DEFAULT_RECIPE_DESCRIPTION = "Test Description";
    private static final int DEFAULT_DURATION_IN_MINUTES = 60;
    private static final int DEFAULT_SERVINGS = 2;
    private static final List<String> DEFAULT_STEPS = List.of("This is step 1", "This is step 2");
    private static final double DEFAULT_QUANTITY = 1.0;
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final UserId USER_ID = UserId.create();
    private static final String USER_NAME = "username";
    private static final String USER_EMAIL = "user@email.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper mapper;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "recipe_ingredients", "recipe_steps", "recipes", "ingredients", "users");
    }

    @Test
    void searchRecipes_shouldReturnAllRecipes_whenRecipesExist() throws Exception {
        // Arrange
        User user = createUser();

        Recipe recipe1 = recipeRepository.save(buildRecipe(user));
        Recipe recipe2 = recipeRepository.save(buildRecipe(user));

        // Act & Assert
        performSearch(defaultRequest())
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
    void searchRecipes_shouldReturnEmptyList_whenNoRecipesExist() throws Exception {
        // Arrange
        createUser();

        // Act & Assert
        performSearch(defaultRequest())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.page.totalPages").value(0));
    }

    @Test
    void searchRecipes_shouldReturnPagedResults_whenPageSizeIsSmall() throws Exception {
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
            recipeRepository.save(buildRecipe(user));
        }

        long totalElements = recipeRepository.count();

        // Act & Assert
        performSearch(new RecipeSearchRequest(List.of(), true, firstPageIndex, pageSize))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(expectedFirstPageCount)))
                .andExpect(jsonPath("$.page.totalElements").value(totalElements))
                .andExpect(jsonPath("$.page.totalPages").value(expectedTotalPages))
                .andExpect(jsonPath("$.page.number").value(firstPageIndex))
                .andExpect(jsonPath("$.page.size").value(pageSize));

        performSearch(new RecipeSearchRequest(List.of(), true, secondPageIndex, pageSize))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(expectedSecondPageCount)))
                .andExpect(jsonPath("$.page.number").value(secondPageIndex))
                .andExpect(jsonPath("$.page.size").value(pageSize));
    }

    @Test
    void searchRecipes_shouldReturn401_whenNotAuthenticated() throws Exception {
        // Arrange
        User user = createUser();
        recipeRepository.save(buildRecipe(user));

        // Act & Assert
        mockMvc.perform(post("/api/recipes/search")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(defaultRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void searchRecipes_shouldReturnEmptyList_whenNoRecipesOfLoggedInUserExist() throws Exception {
        // Arrange
        createUser();
        recipeRepository.save(buildRecipe(new User("email", "name", List.of())));

        // Act & Assert
        performSearch(defaultRequest())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.page.totalPages").value(0));
    }

    @Test
    void searchRecipes_shouldFilterByIngredients_whenIngredientIdsProvided() throws Exception {
        // Arrange
        Ingredient flour = createAndSaveIngredient("Flour", List.of(Category.GRAIN));
        Ingredient sugar = createAndSaveIngredient("Sugar", List.of(Category.GRAIN));
        Ingredient salt = createAndSaveIngredient("Salt", List.of(Category.GRAIN));

        User user = createUser();

        Recipe recipe1 = recipeRepository.save(buildRecipeWithIngredients(List.of(flour, sugar), user));
        Recipe recipe2 = recipeRepository.save(buildRecipeWithIngredients(List.of(flour, sugar, salt), user));
        recipeRepository.save(buildRecipeWithIngredients(List.of(flour), user));
        recipeRepository.save(buildRecipeWithIngredients(List.of(salt), user));

        RecipeSearchRequest dto = new RecipeSearchRequest(List.of(flour.id().id(), sugar.id().id()),
                true, DEFAULT_PAGE, DEFAULT_PAGE_SIZE);

        // Act & Assert
        performSearch(dto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].id", hasItems(
                        recipe1.getId().id().toString(),
                        recipe2.getId().id().toString()
                )))
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    void searchRecipes_shouldReturnEmptyResults_whenNoRecipesMatchIngredientFilter() throws Exception {
        // Arrange
        Ingredient flour = createAndSaveIngredient("Flour", List.of(Category.GRAIN));
        User user = createUser();
        recipeRepository.save(buildRecipeWithIngredients(List.of(flour), user));

        // Act & Assert
        performSearch(new RecipeSearchRequest(List.of(UUID.randomUUID()), true, DEFAULT_PAGE, DEFAULT_PAGE_SIZE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.page.totalElements").value(0));
    }

    @Test
    void searchRecipes_shouldExcludeRecipesConflictingWithPreferences_whenPreferencesAreSet() throws Exception {
        // Arrange
        Ingredient flour = createAndSaveIngredient("Flour", List.of(Category.GRAIN));
        Ingredient sugar = createAndSaveIngredient("Sugar", List.of(Category.GRAIN));
        Ingredient milk = createAndSaveIngredient("Milk", List.of(Category.DAIRY));
        User user = createUser();

        userPreferenceRepository.updatePreferences(USER_ID, new UserPreferences(
                List.of(Category.DAIRY),
                List.of(flour)
        ));

        recipeRepository.save(buildRecipeWithIngredients(List.of(flour), user));
        recipeRepository.save(buildRecipeWithIngredients(List.of(milk), user));
        Recipe expected = recipeRepository.save(buildRecipeWithIngredients(List.of(sugar), user));

        // Act & Assert
        performSearch(defaultRequest())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(expected.getId().id().toString()))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    private RecipeSearchRequest defaultRequest() {
        return new RecipeSearchRequest(List.of(), true, DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
    }

    private ResultActions performSearch(RecipeSearchRequest request) throws Exception {
        return mockMvc.perform(post("/api/recipes/search")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print());
    }

    private Recipe buildRecipe(User user) {
        Ingredient ingredient = createAndSaveIngredient("Ingredient", List.of(Category.GRAIN));
        ingredientRepository.save(ingredient);
        return buildRecipeWithIngredients(List.of(ingredient), user);
    }

    private Recipe buildRecipeWithIngredients(List<Ingredient> ingredients, User user) {
        List<RecipeIngredient> recipeIngredients = ingredients.stream()
                .map(ing -> new RecipeIngredient(ing, DEFAULT_QUANTITY))
                .toList();

        return new Recipe(
                RecipeId.create(),
                new RecipeDetails(
                        DEFAULT_RECIPE_NAME,
                        DEFAULT_RECIPE_DESCRIPTION,
                        DEFAULT_DURATION_IN_MINUTES,
                        DEFAULT_SERVINGS,
                        DEFAULT_STEPS
                ),
                recipeIngredients,
                user.getId()
        );
    }

    private Ingredient createAndSaveIngredient(String name, List<Category> categories) {
        Ingredient ingredient = new Ingredient(new IngredientId(UUID.randomUUID()), name, Unit.GRAM, categories);
        return ingredientRepository.save(ingredient);
    }

    private User createUser() {
        return userRepository.saveUser(new User(USER_ID, USER_NAME, USER_EMAIL, List.of()));
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject(USER_ID.id().toString())
                .claim("email", USER_EMAIL)
                .claim("name", USER_NAME));
    }
}
