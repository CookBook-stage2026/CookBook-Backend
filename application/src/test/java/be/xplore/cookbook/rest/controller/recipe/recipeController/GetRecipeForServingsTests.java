package be.xplore.cookbook.rest.controller.recipe.recipeController;

import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
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

import java.util.Locale;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetRecipeForServingsTests extends BaseIntegrationTest {

    private static final int DESIRED_SERVINGS = 4;
    private static final int SCALED_DURATION_MINUTES = 70;

    private static final String SCALED_INGREDIENT_NAME = "Ingredient";
    private static final double SCALED_INGREDIENT_QUANTITY = 2.0;
    private static final String SCALED_INGREDIENT_UNIT = "GRAM";

    private static final String SCALED_STEP_1 = "Prepare double the ingredients.";
    private static final String SCALED_STEP_2 = "Cook for slightly longer.";

    private static final String MACRO_TYPE = "CALORIES";
    private static final double MACRO_VALUE_PER_UNIT = 250.0;

    private static final String OLLAMA_CHAT_PATH = "/api/chat";

    private static WireMockServer wireMockServer;
    private static String mockAiBaseUrl;

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
    void stubOllamaChat() {
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(OLLAMA_CHAT_PATH))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(buildOllamaResponseBody(buildValidServingsResponse(DESIRED_SERVINGS)))));
    }

    @AfterEach
    void resetWireMock() {
        wireMockServer.resetAll();
    }

    @Override
    protected String[] getTablesToClear() {
        return new String[]{
                "recipe_ingredients",
                "recipes",
                "ingredients",
                "recipe_steps",
                "ingredient_categories"
        };
    }

    @Test
    void getRecipeForServings_shouldReturnScaledRecipe_whenValidRequest() throws Exception {
        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);

        performGetRecipeForServings(recipe.getId().id().toString(), DESIRED_SERVINGS)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(recipe.getId().id().toString()))
                .andExpect(jsonPath("$.name").value(recipe.getName()))
                .andExpect(jsonPath("$.servings").value(DESIRED_SERVINGS))
                .andExpect(jsonPath("$.durationInMinutes").value(SCALED_DURATION_MINUTES))
                .andExpect(jsonPath("$.steps[0]").value(SCALED_STEP_1))
                .andExpect(jsonPath("$.steps[1]").value(SCALED_STEP_2))
                .andExpect(jsonPath("$.ingredients[0].name").value(SCALED_INGREDIENT_NAME))
                .andExpect(jsonPath("$.ingredients[0].quantity").value(SCALED_INGREDIENT_QUANTITY))
                .andExpect(jsonPath("$.ingredients[0].unit").value(SCALED_INGREDIENT_UNIT))
                .andExpect(jsonPath("$.totalMacros[0].type").value(MACRO_TYPE))
                .andExpect(jsonPath("$.totalMacros[0].value").value(MACRO_VALUE_PER_UNIT));
    }

    @Test
    void getRecipeForServings_shouldKeepOriginalIngredients_whenScalingToSameServings() throws Exception {
        User user = createUser();
        Ingredient ingredient = createAndSaveIngredient(SCALED_INGREDIENT_NAME, user);
        Recipe recipe = createAndSaveRecipe(user, ingredient);

        int currentServings = recipe.getServings();
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(OLLAMA_CHAT_PATH))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(buildOllamaResponseBody(buildValidServingsResponse(currentServings)))));

        performGetRecipeForServings(recipe.getId().id().toString(), currentServings)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servings").value(currentServings))
                .andExpect(jsonPath("$.ingredients[0].name").value(SCALED_INGREDIENT_NAME));
    }

    @Test
    void getRecipeForServings_shouldReturn404_whenRecipeNotFound() throws Exception {
        createUser();

        performGetRecipeForServings(UUID.randomUUID().toString(), DESIRED_SERVINGS)
                .andExpect(status().isNotFound());
    }

    @Test
    void getRecipeForServings_shouldReturn404_whenRecipeBelongsToOtherUser() throws Exception {
        User owner = createUserWithId(UserId.create());
        Recipe recipe = createAndSaveRecipe(owner);

        createUser();

        performGetRecipeForServings(recipe.getId().id().toString(), DESIRED_SERVINGS)
                .andExpect(status().isNotFound());
    }

    @Test
    void getRecipeForServings_shouldReturn401_whenNotAuthenticated() throws Exception {
        getMockMvc().perform(get("/api/recipes/{id}/servings/{servings}", UUID.randomUUID(), DESIRED_SERVINGS))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getRecipeForServings_shouldReturn503_whenAiUnavailable() throws Exception {
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(OLLAMA_CHAT_PATH))
                .willReturn(WireMock.aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);

        performGetRecipeForServings(recipe.getId().id().toString(), DESIRED_SERVINGS)
                .andExpect(status().isServiceUnavailable());
    }

    private ResultActions performGetRecipeForServings(String recipeId, int servings) throws Exception {
        return getMockMvc().perform(get("/api/recipes/{id}/servings/{servings}", recipeId, servings)
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    private String buildOllamaResponseBody(String content) {
        record Message(String role, String content) {
        }
        record Response(Message message, boolean done) {
        }

        return getMapper().writeValueAsString(
                new Response(new Message("assistant", content), true)
        );
    }

    private static String buildValidServingsResponse(int servings) {
        return String.format(Locale.US,
                "{"
                        + "\"servings\": %d,"
                        + "\"durationInMinutes\": %d,"
                        + "\"updatedSteps\": [\"%s\", \"%s\"],"
                        + "\"scaledIngredients\": ["
                        + "  {\"name\": \"%s\", \"quantity\": %.1f, \"unit\": \"%s\"}"
                        + "],"
                        + "\"macros\": ["
                        + "  {\"type\": \"%s\", \"valuePerUnit\": %.1f}"
                        + "]"
                        + "}",
                servings,
                SCALED_DURATION_MINUTES,
                SCALED_STEP_1,
                SCALED_STEP_2,
                SCALED_INGREDIENT_NAME,
                SCALED_INGREDIENT_QUANTITY,
                SCALED_INGREDIENT_UNIT,
                MACRO_TYPE,
                MACRO_VALUE_PER_UNIT
        );
    }
}
