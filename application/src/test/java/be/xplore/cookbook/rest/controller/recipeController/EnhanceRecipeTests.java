package be.xplore.cookbook.rest.controller.recipeController;

import be.xplore.cookbook.ai.dto.OllamaResponse;
import be.xplore.cookbook.core.common.Paging;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.response.RecipeDto;
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
    private static final int ENHANCED_DURATION = 75;
    private static final String ENHANCED_STEP_1 = "Wash and dry the thyme.";
    private static final String ENHANCED_STEP_2 = "Add thyme while cooking.";
    private static final Paging FIND_ALL_INGREDIENTS_PAGING = new Paging(0, 10);

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
        String innerJsonContent = buildResponseContent();

        OllamaResponse mockResponse = new OllamaResponse(
                new OllamaResponse.Message("assistant", innerJsonContent),
                true
        );

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/api/chat"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getMapper().writeValueAsString(mockResponse))));
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

        // Act & Assert
        String responseContent = performEnhanceRecipe(originalRecipe.id().id().toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(originalRecipe.id().id().toString())))
                .andExpect(jsonPath("$.name", is(originalRecipe.name())))
                .andExpect(jsonPath("$.durationInMinutes", is(ENHANCED_DURATION)))
                .andExpect(jsonPath("$.ingredients", hasSize(originalRecipe.ingredients().size() + 1)))
                .andExpect(jsonPath("$.steps", hasSize(2)))
                .andExpect(jsonPath("$.steps[0]", is(ENHANCED_STEP_1)))
                .andExpect(jsonPath("$.steps[1]", is(ENHANCED_STEP_2)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        RecipeDto enhancedRecipeDto = getMapper().readValue(responseContent, RecipeDto.class);

        Recipe savedRecipe = getRecipeRepository()
                .findById(originalRecipe.id(), user.id())
                .orElseThrow();

        assertThat(savedRecipe.id().id()).isEqualTo(enhancedRecipeDto.id());
        assertThat(savedRecipe.getSteps()).isNotEqualTo(enhancedRecipeDto.steps());
    }

    @Test
    void enhanceRecipe_shouldNotCreateDuplicate_whenNewIngredientAlreadyExists() throws Exception {
        // Arrange
        User user = createUser();
        createAndSaveIngredient(ENHANCED_INGREDIENT_NAME);
        Recipe recipe = createAndSaveRecipe(user);

        int ingredientCountBefore = getIngredientRepository()
                .searchByNameExcludingIds("", List.of(), FIND_ALL_INGREDIENTS_PAGING)
                .size();

        // Act & Assert
        performEnhanceRecipe(recipe.id().id().toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredients", hasSize(recipe.ingredients().size() + 1)));

        int ingredientCountAfter = getIngredientRepository()
                .searchByNameExcludingIds("", List.of(), FIND_ALL_INGREDIENTS_PAGING)
                .size();

        assertThat(ingredientCountAfter).isEqualTo(ingredientCountBefore);
    }

    @Test
    void enhanceRecipe_shouldReturn404_whenRecipeNotFound() throws Exception {
        // Arrange
        createUser();

        // Act & Assert
        performEnhanceRecipe(UUID.randomUUID().toString())
                .andExpect(status().isNotFound());
    }

    @Test
    void enhanceRecipe_shouldReturn404_whenRecipeBelongsToOtherUser() throws Exception {
        // Arrange
        User owner = createUserWithId(UserId.create());
        Recipe recipe = createAndSaveRecipe(owner);

        createUser();

        // Act & Assert
        performEnhanceRecipe(recipe.id().id().toString())
                .andExpect(status().isNotFound());
    }

    @Test
    void enhanceRecipe_shouldReturn401_whenNotAuthenticated() throws Exception {
        // Act & Assert
        getMockMvc().perform(get("/api/recipes/{id}/enhance", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void enhanceRecipe_shouldReturn502_whenAiReturnsInvalidJson() throws Exception {
        // Arrange
        OllamaResponse mockResponse = new OllamaResponse(
                new OllamaResponse.Message("assistant", "this is invalid json"),
                true
        );

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/api/chat"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getMapper().writeValueAsString(mockResponse))));

        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);

        // Act & Assert
        performEnhanceRecipe(recipe.id().id().toString())
                .andExpect(status().isBadGateway());
    }

    @Test
    void enhanceRecipe_shouldReturn503_whenAiUnavailable() throws Exception {
        // Arrange
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/api/chat"))
                .willReturn(WireMock.aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);

        // Act & Assert
        performEnhanceRecipe(recipe.id().id().toString())
                .andExpect(status().isServiceUnavailable());
    }

    private ResultActions performEnhanceRecipe(String recipeId) throws Exception {
        return getMockMvc().perform(get("/api/recipes/{id}/enhance", recipeId)
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    private static String buildResponseContent() {
        return String.format(Locale.US,
                "{"
                        + "\"durationInMinutes\": %d,"
                        + "\"newIngredient\": {"
                        + "\"name\": \"%s\","
                        + "\"quantity\": %.1f,"
                        + "\"unit\": \"%s\","
                        + "\"categories\": [\"%s\"]"
                        + "},"
                        + "\"updatedSteps\": [\"%s\", \"%s\"]"
                        + "}",
                ENHANCED_DURATION,
                ENHANCED_INGREDIENT_NAME,
                ENHANCED_INGREDIENT_QUANTITY,
                ENHANCED_INGREDIENT_UNIT,
                ENHANCED_INGREDIENT_CATEGORY,
                ENHANCED_STEP_1,
                ENHANCED_STEP_2
        );
    }
}
