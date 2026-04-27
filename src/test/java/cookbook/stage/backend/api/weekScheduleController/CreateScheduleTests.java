package cookbook.stage.backend.api.weekScheduleController;

import cookbook.stage.backend.api.input.CreateDayScheduleDto;
import cookbook.stage.backend.api.input.CreateWeekScheduleDto;
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
import tools.jackson.databind.json.JsonMapper;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class CreateScheduleTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper mapper;

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
    private static final int DEFAULT_DURATION_IN_MINUTES = 30;
    private static final String DEFAULT_RECIPE_DESCRIPTION = "test recipe";
    private static final int DEFAULT_QUANTITY = 5;
    private static final int NUMBER_OF_DAYS_IN_WEEK = 7;
    private static final int AMOUNT_OF_RECIPES = 3;

    @BeforeEach
    void clearDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "recipe_ingredients", "recipe_steps", "day_schedules", "week_schedules",
                "recipes", "ingredients", "users");
    }

    @Test
    void createSchedule_ValidFullWeekSchedule_ShouldCreateAndReturn() throws Exception {
        var user = createUser();
        var recipes = seedRecipesForAllDays(user.getId());

        List<CreateDayScheduleDto> days = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            days.add(new CreateDayScheduleDto(recipes.get(day).getId().id(), day));
        }

        mockMvc.perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(days))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.days", hasSize(NUMBER_OF_DAYS_IN_WEEK)))
                .andExpect(jsonPath("$.days[*].day", containsInAnyOrder(
                        "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY",
                        "FRIDAY", "SATURDAY", "SUNDAY")))
                .andExpect(jsonPath("$.days[?(@.day=='MONDAY')].recipeSummary.name").value("Monday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='TUESDAY')].recipeSummary.name").value("Tuesday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='WEDNESDAY')].recipeSummary.name").value("Wednesday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='THURSDAY')].recipeSummary.name").value("Thursday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='FRIDAY')].recipeSummary.name").value("Friday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='SATURDAY')].recipeSummary.name").value("Saturday Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='SUNDAY')].recipeSummary.name").value("Sunday Recipe"));
    }

    @Test
    void createSchedule_PartialWeekSchedule_ShouldCreateAndReturn() throws Exception {
        var user = createUser();
        var recipe = seedRecipe("Monday Recipe", user.getId());

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.MONDAY),
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.WEDNESDAY),
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.FRIDAY)
        );

        mockMvc.perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(days))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.days", hasSize(AMOUNT_OF_RECIPES)))
                .andExpect(jsonPath("$.days[*].recipeSummary.name",
                        containsInAnyOrder("Monday Recipe", "Monday Recipe", "Monday Recipe")))
                .andExpect(jsonPath("$.days[*].day",
                        containsInAnyOrder("MONDAY", "WEDNESDAY", "FRIDAY")));
    }

    @Test
    void createSchedule_SingleDaySchedule_ShouldCreateAndReturn() throws Exception {
        var user = createUser();
        var recipe = seedRecipe("Single Day Recipe", user.getId());

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.TUESDAY)
        );

        mockMvc.perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(days))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.days", hasSize(1)))
                .andExpect(jsonPath("$.days[0].recipeSummary.name").value("Single Day Recipe"))
                .andExpect(jsonPath("$.days[0].day").value("TUESDAY"));
    }

    @Test
    void createSchedule_EmptySchedule_ShouldReturnOk() throws Exception {
        createUser();
        List<CreateDayScheduleDto> days = List.of();

        mockMvc.perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(days))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void createSchedule_NullDaysList_ShouldReturnBadRequest() throws Exception {
        createUser();

        mockMvc.perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(null))))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSchedule_NonExistentRecipe_ShouldReturnNotFound() throws Exception {
        UUID nonExistentRecipeId = UUID.randomUUID();

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(nonExistentRecipeId, DayOfWeek.MONDAY)
        );

        mockMvc.perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(days))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void createSchedule_UnauthenticatedUser_ShouldReturnUnauthorized() throws Exception {
        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(UUID.randomUUID(), DayOfWeek.MONDAY)
        );

        mockMvc.perform(post("/api/schedules")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(days))))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createSchedule_ReplaceExistingSchedule_ShouldUpdateAndReturn() throws Exception {
        var user = createUser();
        var recipe1 = seedRecipe("Original Recipe", user.getId());
        var recipe2 = seedRecipe("Updated Recipe", user.getId());

        List<CreateDayScheduleDto> initialDays = List.of(
                new CreateDayScheduleDto(recipe1.getId().id(), DayOfWeek.MONDAY)
        );

        mockMvc.perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(initialDays))))
                .andExpect(status().isOk());

        List<CreateDayScheduleDto> updatedDays = List.of(
                new CreateDayScheduleDto(recipe2.getId().id(), DayOfWeek.MONDAY),
                new CreateDayScheduleDto(recipe2.getId().id(), DayOfWeek.TUESDAY)
        );

        mockMvc.perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(updatedDays))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.days", hasSize(2)))
                .andExpect(jsonPath("$.days[?(@.day=='MONDAY')].recipeSummary.name").value("Updated Recipe"))
                .andExpect(jsonPath("$.days[?(@.day=='TUESDAY')].recipeSummary.name").value("Updated Recipe"));
    }

    @Test
    void createSchedule_DuplicateDays_ShouldReturnBadRequest() throws Exception {
        var user = createUser();
        var recipe = seedRecipe("Test Recipe", user.getId());

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.MONDAY),
                new CreateDayScheduleDto(recipe.getId().id(), DayOfWeek.MONDAY)
        );

        mockMvc.perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(days))))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSchedule_InvalidDayScheduleDto_ShouldReturnBadRequest() throws Exception {
        createUser();

        List<CreateDayScheduleDto> days = List.of(
                new CreateDayScheduleDto(null, DayOfWeek.MONDAY)
        );

        mockMvc.perform(post("/api/schedules")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(days))))
                .andDo(print())
                .andExpect(status().isBadRequest());
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
