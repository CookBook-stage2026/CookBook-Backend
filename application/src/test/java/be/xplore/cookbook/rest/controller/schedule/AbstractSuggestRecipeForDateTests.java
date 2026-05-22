package be.xplore.cookbook.rest.controller.schedule;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.ingredient.Macro;
import be.xplore.cookbook.core.domain.ingredient.MacroType;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeDetails;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeIngredient;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.schedule.response.WeekScheduleDto;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class AbstractSuggestRecipeForDateTests extends BaseIntegrationTest {

    private static final LocalDate TARGET_DATE = LocalDate.now().plusDays(2);
    private static final LocalDate TARGET_MONDAY = TARGET_DATE.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    private static final RecipeId RECIPE_ID = RecipeId.create();
    private static final double DEFAULT_CALORIES = 50;

    @BeforeEach
    void stubAi() {
        getWireMockServer().stubFor(WireMock.post(WireMock.urlPathEqualTo("/api/chat"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(buildOllamaResponseBodyWithContent(buildAiResponse()))));
    }

    @AfterEach
    void resetWireMock() {
        getWireMockServer().resetAll();
    }

    @Override
    protected String[] getTablesToClear() {
        return new String[]{
                "recipes", "ingredients", "recipe_ingredients", "recipe_steps",
                "ingredient_categories", "week_schedules", "day_schedules", "users"
        };
    }

    protected abstract WireMockServer getWireMockServer();

    protected abstract MockHttpServletRequestBuilder suggestRequest(LocalDate targetDate);

    protected abstract ScheduleOwner setupOwner(User user);

    protected abstract String getBaseUrl();

    @Test
    void suggestRecipeForDay_shouldReturnUpdatedWeekSchedule_whenValidRequest() throws Exception {
        // Arrange
        User user = createUser();
        Ingredient ingredient = new Ingredient(
                IngredientId.create(), "Ingredient", Unit.GRAM, List.of(Category.EGG), null,
                List.of(
                        new Macro(
                                MacroType.CALORIES,
                                DEFAULT_CALORIES
                        )));
        getIngredientRepository().save(ingredient);
        RecipeIngredient recipeIngredient = new RecipeIngredient(ingredient, 1);
        Recipe recipe = new Recipe(
                RECIPE_ID,
                new RecipeDetails("Recipe", "Description", 1, 1, List.of("Step 1")),
                List.of(recipeIngredient),
                true,
                user
        );
        getRecipeRepository().save(recipe);
        ScheduleOwner owner = setupOwner(user);

        createWeekSchedule(owner,
                Map.of(TARGET_DATE.getDayOfWeek(), recipe),
                TARGET_MONDAY
        );

        // Act & Assert
        getMockMvc().perform(suggestRequest(TARGET_DATE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weekStartDate", notNullValue()))
                .andExpect(jsonPath("$.days", not(empty())))
                .andExpect(jsonPath("$.days[*].recipeSummary.id", hasItem(RECIPE_ID.id().toString())));
    }

    @Test
    void suggestRecipeForDay_shouldReturnUpdatedWeekSchedule_whenOverwritingExistingDaySchedule() throws Exception {
        // Arrange
        User user = createUser();
        Recipe recipe = createRecipe(user);
        ScheduleOwner owner = setupOwner(user);

        Ingredient ingredient = new Ingredient(
                IngredientId.create(), "Ingredient", Unit.GRAM, List.of(Category.EGG), null,
                List.of(
                        new Macro(
                                MacroType.CALORIES,
                                DEFAULT_CALORIES
                        )));
        getIngredientRepository().save(ingredient);
        RecipeIngredient recipeIngredient = new RecipeIngredient(ingredient, 1);
        Recipe recipe = new Recipe(
                RECIPE_ID,
                new RecipeDetails("Recipe", "Description", 1, 1, List.of("Step 1")),
                List.of(recipeIngredient),
                true,
                user
        );
        getRecipeRepository().save(recipe);

        createWeekSchedule(owner,
                Map.of(TARGET_DATE.getDayOfWeek(), recipe),
                TARGET_MONDAY
        );

        // Act & Assert
        getMockMvc().perform(suggestRequest(TARGET_DATE))
                .andExpect(status().isOk());

        getMockMvc().perform(suggestRequest(TARGET_DATE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weekStartDate", notNullValue()))
                .andExpect(jsonPath("$.days", not(empty())))
                .andExpect(jsonPath("$.days[*].recipeSummary.id", hasItem(RECIPE_ID.id().toString())));
    }

    @Test
    void suggestRecipeForDay_shouldSaveNewEmptyWeekSchedule_whenValidRequestAndNoExistingSchedule() throws Exception {
        // Arrange
        User user = createUser();

        Ingredient ingredient = new Ingredient(
                IngredientId.create(), "Ingredient", Unit.GRAM, List.of(Category.EGG), null,
                List.of(
                        new Macro(
                                MacroType.CALORIES,
                                DEFAULT_CALORIES
                        )));
        getIngredientRepository().save(ingredient);
        RecipeIngredient recipeIngredient = new RecipeIngredient(ingredient, 1);
        Recipe recipe = new Recipe(
                RECIPE_ID,
                new RecipeDetails("Recipe", "Description", 1, 1, List.of("Step 1")),
                List.of(recipeIngredient),
                true,
                user
        );
        getRecipeRepository().save(recipe);
        ScheduleOwner owner = setupOwner(user);

        // Act & Assert
        String responseContent = getMockMvc().perform(suggestRequest(TARGET_DATE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.days", not(empty())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        WeekScheduleDto dto = getMapper().readValue(responseContent, WeekScheduleDto.class);
        WeekSchedule savedSchedule = getWeekScheduleRepository().findAllByOwner(owner).getFirst();

        assertThat(savedSchedule.id().id()).isEqualTo(dto.id());
        assertThat(savedSchedule.dailyRecipes()).isEmpty();
    }

    @Test
    void suggestRecipeForDay_shouldReturn401_whenNotAuthenticated() throws Exception {
        User user = createUser();
        setupOwner(user);

        getMockMvc().perform(get(getBaseUrl() + "/{date}", TARGET_DATE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void suggestRecipeForDay_shouldReturn502_whenAiReturnsInvalidResponse() throws Exception {
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/api/chat"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(buildOllamaResponseBodyWithContent("not-json"))));

        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);
        ScheduleOwner owner = setupOwner(user);

        createWeekSchedule(owner,
                Map.of(TARGET_DATE.getDayOfWeek(), recipe),
                TARGET_MONDAY
        );

        getMockMvc().perform(suggestRequest(TARGET_DATE))
                .andExpect(status().isBadGateway());
    }

    @Test
    void suggestRecipeForDay_shouldReturn503_whenAiUnavailable() throws Exception {
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/api/chat"))
                .willReturn(WireMock.aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);
        ScheduleOwner owner = setupOwner(user);

        createWeekSchedule(owner,
                Map.of(TARGET_DATE.getDayOfWeek(), recipe),
                TARGET_MONDAY
        );

        getMockMvc().perform(suggestRequest(TARGET_DATE))
                .andExpect(status().isServiceUnavailable());
    }

    private Recipe createRecipe(User user) {
        Ingredient ingredient = new Ingredient(
                IngredientId.create(), "Ingredient", Unit.GRAM, List.of(Category.EGG), null);
        getIngredientRepository().save(ingredient);
        Recipe recipe = new Recipe(
                RECIPE_ID,
                new RecipeDetails("Recipe", "Description", 1, 1, List.of("Step 1")),
                List.of(new RecipeIngredient(ingredient, 1)),
                true,
                user
        );
        getRecipeRepository().save(recipe);
        return recipe;
    }

    private String buildAiResponse() {
        record AiResponse(String id) {
        }

        return getMapper().writeValueAsString(new AiResponse(RECIPE_ID.id().toString()));
    }

    private String buildOllamaResponseBodyWithContent(String content) {
        record Message(String role, String content) {
        }
        record OllamaResponse(Message message, boolean done) {
        }

        return getMapper().writeValueAsString(new OllamaResponse(new Message("assistant", content), true));
    }
}
