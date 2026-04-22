package cookbook.stage.backend.api.recipeController;

import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.ingredient.IngredientRepository;
import cookbook.stage.backend.domain.ingredient.Unit;
import cookbook.stage.backend.domain.recipe.Recipe;
import cookbook.stage.backend.domain.recipe.RecipeId;
import cookbook.stage.backend.domain.recipe.RecipeIngredient;
import cookbook.stage.backend.domain.recipe.RecipeRepository;
import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.user.UserRepository;
import cookbook.stage.backend.repository.jpa.recipe.RecipeDetails;
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

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItems;
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
class GetAllRecipesTests {

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
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "recipe_ingredients", "recipe_steps", "recipes", "ingredients", "users");
    }

    @Test
    void getAllRecipes_shouldReturnAllRecipes_whenRecipesExist() throws Exception {
        // Arrange
        User user = createUser();

        Recipe recipe1 = recipeRepository.save(buildRecipe(user));
        Recipe recipe2 = recipeRepository.save(buildRecipe(user));

        // Act & Assert
        performGetAllRecipes()
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value(DEFAULT_RECIPE_NAME))
                .andExpect(jsonPath("$.content[0].description").value(DEFAULT_RECIPE_DESCRIPTION))
                .andExpect(jsonPath("$.content[0].durationInMinutes").value(DEFAULT_DURATION_IN_MINUTES))
                .andExpect(jsonPath("$.content[0].id").value(recipe1.getId().id().toString()))
                .andExpect(jsonPath("$.content[1].id").value(recipe2.getId().id().toString()))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    void getAllRecipes_shouldReturnEmptyList_whenNoRecipesExist() throws Exception {
        createUser();

        performGetAllRecipes()
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.page.totalPages").value(0));
    }

    @Test
    void getAllRecipes_shouldReturnPagedResults_whenPageSizeIsSmall() throws Exception {
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
        performGetAllRecipes(firstPageIndex, pageSize)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(expectedFirstPageCount)))
                .andExpect(jsonPath("$.page.totalElements").value(totalElements))
                .andExpect(jsonPath("$.page.totalPages").value(expectedTotalPages))
                .andExpect(jsonPath("$.page.number").value(firstPageIndex))
                .andExpect(jsonPath("$.page.size").value(pageSize));

        // Act & Assert
        performGetAllRecipes(secondPageIndex, pageSize)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(expectedSecondPageCount)))
                .andExpect(jsonPath("$.page.number").value(secondPageIndex))
                .andExpect(jsonPath("$.page.size").value(pageSize));
    }

    @Test
    void getAllRecipes_shouldReturn400_whenPageIsNegative() throws Exception {
        // Arrange
        createUser();

        // Act & Assert
        performGetAllRecipes(-1, DEFAULT_PAGE_SIZE)
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllRecipes_shouldReturn400_whenSizeIsNegative() throws Exception {
        // Arrange
        createUser();

        // Act & Assert
        performGetAllRecipes(DEFAULT_PAGE, -1)
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllRecipes_shouldReturn401_whenNotAuthenticated() throws Exception {
        // Arrange
        User user = createUser();
        recipeRepository.save(buildRecipe(user));

        // Act & Assert
        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllRecipes_shouldReturnEmptyList_whenNoRecipesOfLoggedInUserExist() throws Exception {
        // Arrange
        createUser();
        recipeRepository.save(buildRecipe(new User("email", "name", List.of())));

        // Act & Assert
        performGetAllRecipes()
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.page.totalPages").value(0));
    }

    @Test
    void getAllRecipes_shouldFilterByIngredients_whenIngredientIdsProvided() throws Exception {
        // Arrange
        Ingredient flour = createAndSaveIngredient("Flour");
        Ingredient sugar = createAndSaveIngredient("Sugar");
        Ingredient salt = createAndSaveIngredient("Salt");

        User user = createUser();

        Recipe recipe1 = recipeRepository.save(buildRecipeWithIngredients(List.of(flour, sugar), user));
        Recipe recipe2 = recipeRepository.save(buildRecipeWithIngredients(List.of(flour, sugar, salt), user));
        recipeRepository.save(buildRecipeWithIngredients(List.of(flour), user));
        recipeRepository.save(buildRecipeWithIngredients(List.of(salt), user));

        // Act & Assert
        performGetAllRecipesWithIngredients(List.of(flour.id().id(), sugar.id().id()), DEFAULT_PAGE, DEFAULT_PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].id", hasItems(
                        recipe1.getId().id().toString(),
                        recipe2.getId().id().toString()
                )))
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    void getAllRecipes_shouldReturnEmptyResults_whenNoRecipesMatchIngredientFilter() throws Exception {
        // Arrange
        Ingredient flour = createAndSaveIngredient("Flour");

        User user = createUser();

        recipeRepository.save(buildRecipeWithIngredients(List.of(flour), user));

        // Act & Assert
        performGetAllRecipesWithIngredients(List.of(UUID.randomUUID()), DEFAULT_PAGE, DEFAULT_PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.page.totalElements").value(0));
    }

    private Recipe buildRecipe(User user) {
        Ingredient ingredient = new Ingredient(new IngredientId(UUID.randomUUID()),
                "Flour " + UUID.randomUUID(), Unit.GRAM);
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
                        DEFAULT_STEPS,
                        recipeIngredients
                ),
                user.getId()
        );
    }

    private Ingredient createAndSaveIngredient(String name) {
        Ingredient ingredient = new Ingredient(new IngredientId(UUID.randomUUID()), name, Unit.GRAM);
        return ingredientRepository.save(ingredient);
    }

    private ResultActions performGetAllRecipes() throws Exception {
        return mockMvc.perform(get("/api/recipes")
                        .with(validJwt())
                        .with(csrf()))
                .andDo(print());
    }

    private ResultActions performGetAllRecipes(int page, int size) throws Exception {
        return mockMvc.perform(get("/api/recipes")
                        .with(validJwt())
                        .with(csrf())
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andDo(print());
    }

    private User createUser() {
        return userRepository.save(new User(USER_ID, USER_NAME, USER_EMAIL, List.of()));
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject(USER_ID.id().toString())
                .claim("email", USER_EMAIL)
                .claim("name", USER_NAME));
    }

    private ResultActions performGetAllRecipesWithIngredients(List<UUID> ingredientIds, int page, int size)
            throws Exception {
        var request = get("/api/recipes")
                .with(validJwt())
                .with(csrf())
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size));

        for (UUID id : ingredientIds) {
            request.param("ingredientIds", id.toString());
        }

        return mockMvc.perform(request)
                .andDo(print());
    }
}
