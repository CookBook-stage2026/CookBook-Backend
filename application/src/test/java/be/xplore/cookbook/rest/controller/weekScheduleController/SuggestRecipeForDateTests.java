package be.xplore.cookbook.rest.controller.weekScheduleController;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeDetails;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeIngredient;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.response.WeekScheduleDto;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SuggestRecipeForDateTests extends BaseIntegrationTest {

    private static WireMockServer wireMockServer;
    private static String mockAiBaseUrl;

    private static final LocalDate TARGET_DATE = LocalDate.now().plusDays(2);
    private static final LocalDate TARGET_MONDAY = TARGET_DATE.minusDays(TARGET_DATE.getDayOfWeek().getValue() - 1);
    private static final RecipeId RECIPE_ID = RecipeId.create();

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor(wireMockServer.port());
        mockAiBaseUrl = wireMockServer.baseUrl();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("ollama.base-url", () -> mockAiBaseUrl);
    }

    @BeforeEach
    void stubAi() {
        String response = buildOllamaResponseBodyWithContent(buildAiResponse());
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/api/chat"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
    }

    @AfterEach
    void reset() {
        wireMockServer.resetAll();
    }

    @Override
    protected String[] getTablesToClear() {
        return new String[]{
                "recipes",
                "ingredients",
                "recipe_ingredients",
                "recipe_steps",
                "ingredient_categories",
                "week_schedules",
                "day_schedules",
                "users"
        };
    }

    @Test
    void suggestRecipeForDay_shouldReturnUpdatedWeekSchedule_whenValidRequest() throws Exception {
        // Arrange
        User user = createUser();

        Ingredient ingredient = new Ingredient(IngredientId.create(), "Ingredient", Unit.GRAM, List.of(Category.EGG));
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

        createWeekSchedule(user,
                Map.of(TARGET_DATE.getDayOfWeek(), recipe),
                TARGET_MONDAY
        );

        // Act & Assert
        performSuggest(TARGET_DATE.toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weekStartDate", notNullValue()))
                .andExpect(jsonPath("$.days", not(empty())))
                .andExpect(jsonPath("$.days[*].recipeSummary.id", hasItem(RECIPE_ID.id().toString())));
    }

    @Test
    void suggestRecipeForDay_shouldReturnUpdatedWeekSchedule_whenOverwritingExistingDaySchedule() throws Exception {
        // Arrange
        User user = createUser();

        Ingredient ingredient = new Ingredient(IngredientId.create(), "Ingredient", Unit.GRAM, List.of(Category.EGG));
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

        createWeekSchedule(user,
                Map.of(TARGET_DATE.getDayOfWeek(), recipe),
                TARGET_MONDAY
        );

        // Act & Assert
        performSuggest(TARGET_DATE.toString())
                .andExpect(status().isOk());

        performSuggest(TARGET_DATE.toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weekStartDate", notNullValue()))
                .andExpect(jsonPath("$.days", not(empty())))
                .andExpect(jsonPath("$.days[*].recipeSummary.id", hasItem(RECIPE_ID.id().toString())));
    }

    @Test
    void suggestRecipeForDay_shouldSaveNewEmptyWeekSchedule_whenValidRequestAndNoExistingSchedule() throws Exception {
        // Arrange
        User user = createUser();

        Ingredient ingredient = new Ingredient(IngredientId.create(), "Ingredient", Unit.GRAM, List.of(Category.EGG));
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

        // Act & Assert
        String responseContent = performSuggest(TARGET_DATE.toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.days", not(empty())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        WeekScheduleDto dto = getMapper().readValue(responseContent, WeekScheduleDto.class);
        WeekSchedule savedSchedule = getWeekScheduleRepository().findAllByUserId(user.id()).getFirst();

        assertThat(savedSchedule.id().id()).isEqualTo(dto.id());
        assertThat(savedSchedule.dailyRecipes()).isEmpty();
    }

    @Test
    void suggestRecipeForDay_shouldReturn401_whenNotAuthenticated() throws Exception {
        getMockMvc().perform(get("/api/schedules/suggest/{date}", TARGET_DATE)
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

        createWeekSchedule(user,
                Map.of(TARGET_DATE.getDayOfWeek(), recipe),
                TARGET_MONDAY
        );

        performSuggest(TARGET_DATE.toString())
                .andExpect(status().isBadGateway());
    }

    @Test
    void suggestRecipeForDay_shouldReturn503_whenAiUnavailable() throws Exception {
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/api/chat"))
                .willReturn(WireMock.aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);

        createWeekSchedule(user,
                Map.of(TARGET_DATE.getDayOfWeek(), recipe),
                TARGET_MONDAY
        );

        performSuggest(TARGET_DATE.toString())
                .andExpect(status().isServiceUnavailable());
    }

    private ResultActions performSuggest(String date) throws Exception {
        return getMockMvc().perform(
                        get("/api/schedules/suggest/{date}", date)
                                .with(validJwt())
                                .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private String buildAiResponse() {
        record AiResponse(String id) {
        }

        return getMapper().writeValueAsString(
                new AiResponse(RECIPE_ID.id().toString())
        );
    }

    private String buildOllamaResponseBodyWithContent(String content) {
        record Message(String role, String content) {
        }
        record OllamaResponse(Message message, boolean done) {
        }

        return getMapper().writeValueAsString(
                new OllamaResponse(new Message("assistant", content), true)
        );
    }
}
