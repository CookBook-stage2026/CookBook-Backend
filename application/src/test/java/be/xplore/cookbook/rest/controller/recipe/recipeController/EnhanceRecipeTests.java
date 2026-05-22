package be.xplore.cookbook.rest.controller.recipe.recipeController;

import be.xplore.cookbook.core.common.Paging;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.recipe.response.RecipeDto;
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

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EnhanceRecipeTests extends BaseIntegrationTest {

    private static final String ENHANCED_INGREDIENT_NAME = "Fresh Thyme";
    private static final String ENHANCED_INGREDIENT_UNIT = "GRAM";
    private static final String ENHANCED_INGREDIENT_CATEGORY = "HERB";
    private static final double ENHANCED_INGREDIENT_QUANTITY = 5.0;
    private static final int ENHANCED_DURATION_MINUTES = 75;
    private static final String ENHANCED_STEP_1 = "Wash and dry the thyme.";
    private static final String ENHANCED_STEP_2 = "Add thyme while cooking.";
    private static final String ENHANCED_MACRO_TYPE = "CALORIES";
    private static final double ENHANCED_MACRO_VALUE = 10.0;

    private static final int PAGING_DEFAULT_PAGE = 0;
    private static final int PAGING_DEFAULT_SIZE = 10;
    private static final Paging FIND_ALL_INGREDIENTS_PAGING = new Paging(PAGING_DEFAULT_PAGE, PAGING_DEFAULT_SIZE);

    private static final int EXPECTED_INGREDIENT_COUNT_INCREASE = 1;
    private static final int EXPECTED_STEPS_COUNT = 2;

    private static final String OLLAMA_CHAT_PATH = "/api/chat";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String MESSAGE_ROLE_ASSISTANT = "assistant";
    private static final boolean RESPONSE_DONE_TRUE = true;

    private static final String ENHANCE_RECIPE_PATH = "/api/recipes/{id}/enhance";

    private static final String INVALID_JSON_RESPONSE = "this is not valid json";

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
        String responseBody = buildOllamaResponseBodyWithContent(buildValidEnhancementResponseContent());
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(OLLAMA_CHAT_PATH))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", CONTENT_TYPE_JSON)
                        .withBody(responseBody)));
    }

    @AfterEach
    void resetWireMock() {
        if (!wireMockServer.isRunning()) {
            wireMockServer.start();
        }
        wireMockServer.resetAll();
    }

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"recipe_ingredients", "recipes", "ingredients", "recipe_steps", "ingredient_categories"};
    }

    @Test
    void enhanceRecipe_shouldReturnEnhancedRecipeWithoutSaving_whenValidRequest() throws Exception {
        // Arrange
        User user = createUser();
        Recipe originalRecipe = createAndSaveRecipe(user);
        int originalIngredientCount = originalRecipe.getIngredients().size();

        // Act & Assert
        String responseContent = performEnhanceRecipe(originalRecipe.getId().id().toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(originalRecipe.getId().id().toString())))
                .andExpect(jsonPath("$.name", is(originalRecipe.getName())))
                .andExpect(jsonPath("$.durationInMinutes", is(ENHANCED_DURATION_MINUTES)))
                .andExpect(jsonPath("$.ingredients", hasSize(originalIngredientCount
                        + EXPECTED_INGREDIENT_COUNT_INCREASE)))
                .andExpect(jsonPath("$.steps", hasSize(EXPECTED_STEPS_COUNT)))
                .andExpect(jsonPath("$.steps[0]", is(ENHANCED_STEP_1)))
                .andExpect(jsonPath("$.steps[1]", is(ENHANCED_STEP_2)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        RecipeDto enhancedRecipeDto = getMapper().readValue(responseContent, RecipeDto.class);

        Recipe savedRecipe = getRecipeRepository()
                .findById(originalRecipe.getId(), user)
                .orElseThrow();

        assertThat(savedRecipe.getId().id()).isEqualTo(enhancedRecipeDto.id());
        assertThat(savedRecipe.getSteps()).isNotEqualTo(enhancedRecipeDto.steps());
        assertThat(savedRecipe.getDurationInMinutes()).isNotEqualTo(ENHANCED_DURATION_MINUTES);
    }

    @Test
    void enhanceRecipe_shouldNotCreateDuplicate_whenNewIngredientAlreadyExists() throws Exception {
        // Arrange
        User user = createUser();
        createAndSaveIngredient(ENHANCED_INGREDIENT_NAME);
        Recipe recipe = createAndSaveRecipe(user);
        int originalIngredientCount = recipe.getIngredients().size();

        int ingredientCountBefore = getIngredientRepository()
                .searchByNameExcludingIds("", List.of(), FIND_ALL_INGREDIENTS_PAGING, user)
                .size();

        // Act & Assert
        performEnhanceRecipe(recipe.getId().id().toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredients", hasSize(originalIngredientCount
                        + EXPECTED_INGREDIENT_COUNT_INCREASE)));

        int ingredientCountAfter = getIngredientRepository()
                .searchByNameExcludingIds("", List.of(), FIND_ALL_INGREDIENTS_PAGING, user)
                .size();

        assertThat(ingredientCountAfter).isEqualTo(ingredientCountBefore);
    }

    @Test
    void enhanceRecipe_shouldReturn404_whenRecipeNotFound() throws Exception {
        // Arrange
        createUser();
        String nonExistentRecipeId = UUID.randomUUID().toString();

        // Act & Assert
        performEnhanceRecipe(nonExistentRecipeId)
                .andExpect(status().isNotFound());
    }

    @Test
    void enhanceRecipe_shouldReturn404_whenRecipeBelongsToOtherUser() throws Exception {
        // Arrange
        User owner = createUserWithId(UserId.create());
        Recipe recipe = createAndSaveRecipe(owner);

        createUser();

        // Act & Assert
        performEnhanceRecipe(recipe.getId().id().toString())
                .andExpect(status().isNotFound());
    }

    @Test
    void enhanceRecipe_shouldReturn401_whenNotAuthenticated() throws Exception {
        // Act & Assert
        getMockMvc().perform(get(ENHANCE_RECIPE_PATH, UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void enhanceRecipe_shouldReturn502_whenAiReturnsInvalidResponse() throws Exception {
        // Arrange
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(OLLAMA_CHAT_PATH))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", CONTENT_TYPE_JSON)
                        .withBody(buildOllamaResponseBodyWithContent(INVALID_JSON_RESPONSE))));

        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);

        // Act & Assert
        performEnhanceRecipe(recipe.getId().id().toString())
                .andExpect(status().isBadGateway());
    }

    @Test
    void enhanceRecipe_shouldReturn502_whenAiReturnsValidJsonButInvalidStructure() throws Exception {
        // Arrange
        String invalidStructure = "{\"invalid\": \"structure\"}";
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(OLLAMA_CHAT_PATH))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", CONTENT_TYPE_JSON)
                        .withBody(buildOllamaResponseBodyWithContent(invalidStructure))));

        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);

        // Act & Assert
        performEnhanceRecipe(recipe.getId().id().toString())
                .andExpect(status().isBadGateway());
    }

    @Test
    void enhanceRecipe_shouldReturn503_whenAiUnavailable() throws Exception {
        // Arrange
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(OLLAMA_CHAT_PATH))
                .willReturn(WireMock.aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);

        // Act & Assert
        performEnhanceRecipe(recipe.getId().id().toString())
                .andExpect(status().isServiceUnavailable());
    }

    private ResultActions performEnhanceRecipe(String recipeId) throws Exception {
        return getMockMvc().perform(get(ENHANCE_RECIPE_PATH, recipeId)
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    private String buildOllamaResponseBodyWithContent(String content) {
        record Message(String role, String content) {
        }

        record OllamaResponse(Message message, boolean done) {
        }

        return getMapper().writeValueAsString(
                new OllamaResponse(new Message(MESSAGE_ROLE_ASSISTANT, content), RESPONSE_DONE_TRUE)
        );
    }

    private static String buildValidEnhancementResponseContent() {
        return String.format(Locale.US,
                "{"
                        + "\"durationInMinutes\": %d,"
                        + "\"newIngredient\": {"
                        + "\"name\": \"%s\","
                        + "\"quantity\": %.1f,"
                        + "\"unit\": \"%s\","
                        + "\"categories\": [\"%s\"],"
                        + "\"macros\": ["
                        + "{\"type\": \"%s\", \"valuePerUnit\": %.1f}"
                        + "]"
                        + "},"
                        + "\"updatedSteps\": [\"%s\", \"%s\"]"
                        + "}",
                ENHANCED_DURATION_MINUTES,
                ENHANCED_INGREDIENT_NAME,
                ENHANCED_INGREDIENT_QUANTITY,
                ENHANCED_INGREDIENT_UNIT,
                ENHANCED_INGREDIENT_CATEGORY,
                ENHANCED_MACRO_TYPE,
                ENHANCED_MACRO_VALUE,
                ENHANCED_STEP_1,
                ENHANCED_STEP_2
        );
    }
}
