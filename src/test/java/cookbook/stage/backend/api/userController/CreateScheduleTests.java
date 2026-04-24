package cookbook.stage.backend.api.userController;

import cookbook.stage.backend.api.input.CreateWeekScheduleDto;
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
import tools.jackson.databind.json.JsonMapper;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerCreateScheduleTests {

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
    private static final int AMOUNT_OF_RECIPES_NORMAL_SCHEDULE = 3;
    private static final int AMOUNT_OF_RECIPES_UPDATED_SCHEDULE = 2;

    @BeforeEach
    void clearDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "recipe_ingredients", "recipe_steps", "week_schedules", "recipes", "ingredients", "users");
    }

    @Test
    void createSchedule_ValidFullWeekSchedule_ShouldCreateAndReturn() throws Exception {
        // Arrange
        var user = createUser();
        var recipes = seedRecipesForAllDays(user.getId());

        Map<DayOfWeek, UUID> scheduleData = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            scheduleData.put(day, recipes.get(day).getId().id());
        }

        // Act & Assert
        mockMvc.perform(post("/api/user/schedule")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(scheduleData))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.dailyRecipes.MONDAY.name").value("Monday Recipe"))
                .andExpect(jsonPath("$.dailyRecipes.TUESDAY.name").value("Tuesday Recipe"))
                .andExpect(jsonPath("$.dailyRecipes.WEDNESDAY.name").value("Wednesday Recipe"))
                .andExpect(jsonPath("$.dailyRecipes.THURSDAY.name").value("Thursday Recipe"))
                .andExpect(jsonPath("$.dailyRecipes.FRIDAY.name").value("Friday Recipe"))
                .andExpect(jsonPath("$.dailyRecipes.SATURDAY.name").value("Saturday Recipe"))
                .andExpect(jsonPath("$.dailyRecipes.SUNDAY.name").value("Sunday Recipe"));
    }

    @Test
    void createSchedule_PartialWeekSchedule_ShouldCreateAndReturn() throws Exception {
        // Arrange
        var user = createUser();
        var recipe = seedRecipe("Monday Recipe", user.getId());

        Map<DayOfWeek, UUID> scheduleData = new EnumMap<>(DayOfWeek.class);
        scheduleData.put(DayOfWeek.MONDAY, recipe.getId().id());
        scheduleData.put(DayOfWeek.WEDNESDAY, recipe.getId().id());
        scheduleData.put(DayOfWeek.FRIDAY, recipe.getId().id());

        // Act & Assert
        mockMvc.perform(post("/api/user/schedule")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(scheduleData))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.dailyRecipes.MONDAY.name").value("Monday Recipe"))
                .andExpect(jsonPath("$.dailyRecipes.WEDNESDAY.name").value("Monday Recipe"))
                .andExpect(jsonPath("$.dailyRecipes.FRIDAY.name").value("Monday Recipe"))
                .andExpect(jsonPath("$.dailyRecipes.size()").value(AMOUNT_OF_RECIPES_NORMAL_SCHEDULE));
    }

    @Test
    void createSchedule_SingleDaySchedule_ShouldCreateAndReturn() throws Exception {
        // Arrange
        var user = createUser();
        var recipe = seedRecipe("Single Day Recipe", user.getId());

        Map<DayOfWeek, UUID> scheduleData = new EnumMap<>(DayOfWeek.class);
        scheduleData.put(DayOfWeek.TUESDAY, recipe.getId().id());

        // Act & Assert
        mockMvc.perform(post("/api/user/schedule")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(scheduleData))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.dailyRecipes.TUESDAY.name").value("Single Day Recipe"))
                .andExpect(jsonPath("$.dailyRecipes.size()").value(1));
    }

    @Test
    void createSchedule_EmptySchedule_ShouldCreateAndReturn() throws Exception {
        // Arrange
        createUser();
        Map<DayOfWeek, UUID> scheduleData = new EnumMap<>(DayOfWeek.class);

        // Act & Assert
        mockMvc.perform(post("/api/user/schedule")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(scheduleData))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void createSchedule_NonExistentRecipe_ShouldReturnNotFound() throws Exception {
        // Arrange
        UUID nonExistentRecipeId = UUID.randomUUID();

        Map<DayOfWeek, UUID> scheduleData = new EnumMap<>(DayOfWeek.class);
        scheduleData.put(DayOfWeek.MONDAY, nonExistentRecipeId);

        // Act & Assert
        mockMvc.perform(post("/api/user/schedule")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(scheduleData))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void createSchedule_UnauthenticatedUser_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        Map<DayOfWeek, UUID> scheduleData = new EnumMap<>(DayOfWeek.class);
        scheduleData.put(DayOfWeek.MONDAY, UUID.randomUUID());

        // Act & Assert
        mockMvc.perform(post("/api/user/schedule")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(scheduleData))))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createSchedule_ReplaceExistingSchedule_ShouldUpdateAndReturn() throws Exception {
        // Arrange
        var user = createUser();
        var recipe1 = seedRecipe("Original Recipe", user.getId());
        var recipe2 = seedRecipe("Updated Recipe", user.getId());

        Map<DayOfWeek, UUID> initialSchedule = new EnumMap<>(DayOfWeek.class);
        initialSchedule.put(DayOfWeek.MONDAY, recipe1.getId().id());

        mockMvc.perform(post("/api/user/schedule")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(initialSchedule))))
                .andExpect(status().isOk());

        // Act & Assert
        Map<DayOfWeek, UUID> updatedSchedule = new EnumMap<>(DayOfWeek.class);
        updatedSchedule.put(DayOfWeek.MONDAY, recipe2.getId().id());
        updatedSchedule.put(DayOfWeek.TUESDAY, recipe2.getId().id());

        mockMvc.perform(post("/api/user/schedule")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CreateWeekScheduleDto(updatedSchedule))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.dailyRecipes.MONDAY.name").value("Updated Recipe"))
                .andExpect(jsonPath("$.dailyRecipes.TUESDAY.name").value("Updated Recipe"))
                .andExpect(jsonPath("$.dailyRecipes.size()").value(AMOUNT_OF_RECIPES_UPDATED_SCHEDULE));
    }

    private User createUser() {
        return userRepository.saveUser(new User(USER_ID, USER_NAME, USER_EMAIL, List.of()));
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
                        2, steps, recipeIngredients),
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
        Ingredient ingredient = new Ingredient(new IngredientId(UUID.randomUUID()), name, Unit.GRAM);
        return ingredientRepository.save(ingredient);
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject(USER_ID.id().toString())
                .claim("email", USER_EMAIL)
                .claim("name", USER_NAME));
    }
}
