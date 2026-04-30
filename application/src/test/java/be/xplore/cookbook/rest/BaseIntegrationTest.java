package be.xplore.cookbook.rest;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeDetails;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeIngredient;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.core.domain.weekschedule.DaySchedule;
import be.xplore.cookbook.core.domain.weekschedule.DayScheduleId;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.core.domain.weekschedule.WeekScheduleId;
import be.xplore.cookbook.core.repository.IngredientRepository;
import be.xplore.cookbook.core.repository.RecipeRepository;
import be.xplore.cookbook.core.repository.UserPreferenceRepository;
import be.xplore.cookbook.core.repository.UserRepository;
import be.xplore.cookbook.core.repository.WeekScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public abstract class BaseIntegrationTest {

    protected static final UserId USER_ID = UserId.create();
    protected static final String USER_NAME = "username";
    protected static final String USER_EMAIL = "user@email.com";

    private static final String DEFAULT_RECIPE_NAME = "Test Name";
    private static final String DEFAULT_RECIPE_DESCRIPTION = "Test Description";
    private static final int DEFAULT_DURATION_IN_MINUTES = 60;
    private static final int DEFAULT_SERVINGS = 2;
    private static final List<String> DEFAULT_STEPS = List.of("This is step 1", "This is step 2");
    private static final double DEFAULT_QUANTITY = 1.0;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private WeekScheduleRepository weekScheduleRepository;

    @Autowired
    private JsonMapper mapper;

    @BeforeEach
    void clearDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, getTablesToClear());
    }

    protected abstract String[] getTablesToClear();

    protected void seedWeekSchedule(User user, Map<DayOfWeek, Recipe> dailyRecipes) {
        List<DaySchedule> daySchedules = new ArrayList<>();
        dailyRecipes.forEach((day, recipe) ->
                daySchedules.add(new DaySchedule(DayScheduleId.create(), recipe, day))
        );

        WeekSchedule weekSchedule = new WeekSchedule(WeekScheduleId.create(), user, daySchedules);
        getWeekScheduleRepository().save(weekSchedule);
    }

    protected User createUser() {
        User user = userRepository.save(new User(USER_ID, USER_NAME, USER_EMAIL, List.of()));
        userPreferenceRepository.save(UserPreferences.empty(user));
        return user;
    }

    protected Recipe createAndSaveRecipe(User user) {
        Ingredient ingredient = createAndSaveIngredient("Ingredient");
        return createAndSaveRecipe(DEFAULT_RECIPE_NAME, DEFAULT_RECIPE_DESCRIPTION,
                DEFAULT_DURATION_IN_MINUTES, DEFAULT_SERVINGS, DEFAULT_STEPS,
                List.of(ingredient), user);
    }

    protected Recipe createAndSaveRecipe(String name, User user) {
        Ingredient ingredient = createAndSaveIngredient("Ingredient");
        return createAndSaveRecipe(name, DEFAULT_RECIPE_DESCRIPTION,
                DEFAULT_DURATION_IN_MINUTES, DEFAULT_SERVINGS, DEFAULT_STEPS,
                List.of(ingredient), user);
    }

    protected Recipe createAndSaveRecipeWithIngredients(List<Ingredient> ingredients, User user) {
        return createAndSaveRecipe(DEFAULT_RECIPE_NAME, DEFAULT_RECIPE_DESCRIPTION,
                DEFAULT_DURATION_IN_MINUTES, DEFAULT_SERVINGS, DEFAULT_STEPS,
                ingredients, user);
    }

    private Recipe createAndSaveRecipe(String name, String description, int durationInMinutes,
                                       int servings, List<String> steps, List<Ingredient> ingredients,
                                       User user) {
        List<RecipeIngredient> recipeIngredients = ingredients.stream()
                .map(ing -> new RecipeIngredient(ing, DEFAULT_QUANTITY))
                .toList();

        Recipe recipe = new Recipe(
                RecipeId.create(),
                new RecipeDetails(name, description, durationInMinutes, servings, steps),
                recipeIngredients,
                user.id()
        );
        return recipeRepository.save(recipe);
    }

    protected Ingredient createAndSaveIngredient(String name) {
        return createAndSaveIngredient(name, Unit.GRAM, Category.ADDITIVE);
    }

    protected Ingredient createAndSaveIngredient(String name, Unit unit, Category category) {
        Ingredient ingredient = new Ingredient(IngredientId.create(), name, unit, List.of(category));
        return ingredientRepository.save(ingredient);
    }

    protected SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject(USER_ID.id().toString())
                .claim("email", USER_EMAIL)
                .claim("name", USER_NAME));
    }

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public IngredientRepository getIngredientRepository() {
        return ingredientRepository;
    }

    public RecipeRepository getRecipeRepository() {
        return recipeRepository;
    }

    public UserPreferenceRepository getUserPreferenceRepository() {
        return userPreferenceRepository;
    }

    public WeekScheduleRepository getWeekScheduleRepository() {
        return weekScheduleRepository;
    }

    public JsonMapper getMapper() {
        return mapper;
    }
}
