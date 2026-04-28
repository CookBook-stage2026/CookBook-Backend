package cookbook.stage.backend.api.weekScheduleController;

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
import cookbook.stage.backend.domain.user.UserRepository;
import cookbook.stage.backend.domain.weekschedule.DaySchedule;
import cookbook.stage.backend.domain.weekschedule.DayScheduleId;
import cookbook.stage.backend.domain.weekschedule.WeekSchedule;
import cookbook.stage.backend.domain.weekschedule.WeekScheduleId;
import cookbook.stage.backend.domain.weekschedule.WeekScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class FindForUserTests {

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

    @Autowired
    private WeekScheduleRepository weekScheduleRepository;

    private static final UserId USER_ID = UserId.create();
    private static final String USER_NAME = "username";
    private static final String USER_EMAIL = "user@email.com";
    private static final int DEFAULT_DURATION_IN_MINUTES = 30;
    private static final String DEFAULT_RECIPE_DESCRIPTION = "test recipe";
    private static final int DEFAULT_QUANTITY = 5;
    private static final int DAYS_IN_WEEK = 7;
    private static final int DAYS_WITH_RECIPE = 3;

    @BeforeEach
    void clearDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "recipe_ingredients", "recipe_steps", "day_schedules", "week_schedules",
                "recipes", "ingredients", "users");
    }

    @Test
    void findForUser_ExistingSchedule_ShouldReturnSchedule() throws Exception {
        var user = createUser();
        var recipes = seedRecipesForAllDays(user.getId());
        seedWeekSchedule(user, recipes);

        mockMvc.perform(get("/api/schedules/user")
                        .with(validJwt())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.days", hasSize(DAYS_IN_WEEK)))
                .andExpect(jsonPath("$.days[?(@.day=='MONDAY')].recipeSummary.name").value("Monday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='TUESDAY')].recipeSummary.name").value("Tuesday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='WEDNESDAY')].recipeSummary.name").value("Wednesday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='THURSDAY')].recipeSummary.name").value("Thursday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='FRIDAY')].recipeSummary.name").value("Friday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='SATURDAY')].recipeSummary.name").value("Saturday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='SUNDAY')].recipeSummary.name").value("Sunday Recipe"));
    }

    @Test
    void findForUser_PartialSchedule_ShouldReturnSchedule() throws Exception {
        var user = createUser();
        var recipe = seedRecipe("Monday Recipe", user.getId());

        Map<DayOfWeek, Recipe> partialSchedule = new EnumMap<>(DayOfWeek.class);
        partialSchedule.put(DayOfWeek.MONDAY, recipe);
        partialSchedule.put(DayOfWeek.WEDNESDAY, recipe);
        partialSchedule.put(DayOfWeek.FRIDAY, recipe);
        seedWeekSchedule(user, partialSchedule);

        mockMvc.perform(get("/api/schedules/user")
                        .with(validJwt())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.days", hasSize(DAYS_WITH_RECIPE)))
                .andExpect(jsonPath("$.days[?(@.day=='MONDAY')].recipeSummary.name").value("Monday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='WEDNESDAY')].recipeSummary.name").value("Monday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='FRIDAY')].recipeSummary.name").value("Monday Recipe"));
    }

    @Test
    void findForUser_NoSchedule_ShouldReturnNotFound() throws Exception {
        createUser();

        mockMvc.perform(get("/api/schedules/user")
                        .with(validJwt())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void findForUser_UnauthenticatedUser_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/schedules/user")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    private User createUser() {
        return userRepository.save(new User(USER_ID, USER_NAME, USER_EMAIL, List.of()));
    }

    private Recipe seedRecipe(String name, UserId userId) {
        var steps = new ArrayList<String>();
        steps.add("step 1");
        steps.add("step 2");

        var ingredients = new ArrayList<Ingredient>();
        ingredients.add(createAndSaveIngredient("flour"));
        List<RecipeIngredient> recipeIngredients = ingredients.stream()
                .map(ing -> new RecipeIngredient(ing, DEFAULT_QUANTITY))
                .toList();

        Recipe recipe = new Recipe(
                new RecipeId(UUID.randomUUID()),
                new RecipeDetails(name, DEFAULT_RECIPE_DESCRIPTION, DEFAULT_DURATION_IN_MINUTES,
                        2, steps),
                recipeIngredients,
                userId
        );
        return recipeRepository.save(recipe);
    }

    private Map<DayOfWeek, Recipe> seedRecipesForAllDays(UserId userId) {
        Map<DayOfWeek, Recipe> recipes = new EnumMap<>(DayOfWeek.class);
        String[] dayNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        for (int i = 0; i < DayOfWeek.values().length; i++) {
            String recipeName = dayNames[i] + " Recipe";
            recipes.put(DayOfWeek.values()[i], seedRecipe(recipeName, userId));
        }
        return recipes;
    }

    private void seedWeekSchedule(User user, Map<DayOfWeek, Recipe> dailyRecipes) {
        List<DaySchedule> daySchedules = new ArrayList<>();
        dailyRecipes.forEach((day, recipe) ->
                daySchedules.add(new DaySchedule(DayScheduleId.create(), recipe, day))
        );

        WeekSchedule weekSchedule = new WeekSchedule(WeekScheduleId.create(), user, daySchedules);
        weekScheduleRepository.save(weekSchedule);
    }

    private Ingredient createAndSaveIngredient(String name) {
        Ingredient ingredient = new Ingredient(new IngredientId(UUID.randomUUID()), name, Unit.GRAM,
                List.of(Category.EGG));
        return ingredientRepository.save(ingredient);
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject(USER_ID.id().toString())
                .claim("email", USER_EMAIL)
                .claim("name", USER_NAME));
    }
}
