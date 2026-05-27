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
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
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

    private static final String MACRO_TYPE = "CALORIES";
    private static final double MACRO_VALUE_PER_UNIT = 10.0;

    private static final int PAGING_PAGE = 0;
    private static final int PAGING_SIZE = 10;

    private static final Paging PAGING = new Paging(PAGING_PAGE, PAGING_SIZE);

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
                        .withBody(buildOllamaResponseBody(buildValidEnhancementResponse()))));
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
    void enhanceRecipe_shouldReturnEnhancedRecipe_whenValidRequest() throws Exception {
        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);

        ResultActions result = performEnhanceRecipe(recipe.getId().id().toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(recipe.getId().id().toString()))
                .andExpect(jsonPath("$.name").value(recipe.getName()))
                .andExpect(jsonPath("$.durationInMinutes").value(ENHANCED_DURATION_MINUTES))
                .andExpect(jsonPath("$.steps", hasSize(2)))
                .andExpect(jsonPath("$.steps", hasItem(ENHANCED_STEP_1)))
                .andExpect(jsonPath("$.steps", hasItem(ENHANCED_STEP_2)))
                .andExpect(jsonPath("$.ingredients", not(empty())))
                .andExpect(jsonPath("$.ingredients", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.totalMacros", not(empty())))
                .andExpect(jsonPath("$.totalMacros[0].type").value(MACRO_TYPE))
                .andExpect(jsonPath("$.totalMacros[0].value").value(MACRO_VALUE_PER_UNIT));

        String body = result.andReturn().getResponse().getContentAsString();
        RecipeDto dto = getMapper().readValue(body, RecipeDto.class);

        Recipe saved = getRecipeRepository().findById(recipe.getId(), user).orElseThrow();

        assertThat(saved.getId()).isEqualTo(recipe.getId());
        assertThat(dto.id()).isEqualTo(recipe.getId().id());

        assertThat(dto.totalMacros()).isNotEmpty();
    }

    @Test
    void enhanceRecipe_shouldNotCreateDuplicate_whenIngredientAlreadyExists() throws Exception {
        User user = createUser();

        createAndSaveIngredient(ENHANCED_INGREDIENT_NAME);
        Recipe recipe = createAndSaveRecipe(user);

        int before = getIngredientRepository()
                .searchByNameExcludingIds("", List.of(), PAGING, user)
                .size();

        performEnhanceRecipe(recipe.getId().id().toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredients", not(empty())));

        int after = getIngredientRepository()
                .searchByNameExcludingIds("", List.of(), PAGING, user)
                .size();

        assertThat(after).isEqualTo(before);
    }

    @Test
    void enhanceRecipe_shouldReturn404_whenRecipeNotFound() throws Exception {
        createUser();

        performEnhanceRecipe(UUID.randomUUID().toString())
                .andExpect(status().isNotFound());
    }

    @Test
    void enhanceRecipe_shouldReturn404_whenRecipeBelongsToOtherUser() throws Exception {
        User owner = createUserWithId(UserId.create());
        Recipe recipe = createAndSaveRecipe(owner);

        createUser();

        performEnhanceRecipe(recipe.getId().id().toString())
                .andExpect(status().isNotFound());
    }

    @Test
    void enhanceRecipe_shouldReturn401_whenNotAuthenticated() throws Exception {
        getMockMvc().perform(get("/api/recipes/{id}/enhance", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void enhanceRecipe_shouldReturn502_whenAiUnavailable() throws Exception {
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(OLLAMA_CHAT_PATH))
                .willReturn(WireMock.aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);

        performEnhanceRecipe(recipe.getId().id().toString())
                .andExpect(status().isServiceUnavailable());
    }

    private ResultActions performEnhanceRecipe(String id) throws Exception {
        return getMockMvc().perform(get("/api/recipes/{id}/enhance", id)
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

    private static String buildValidEnhancementResponse() {
        return String.format(Locale.US,
                "{"
                        + "\"durationInMinutes\": %d,"
                        + "\"newIngredient\": {"
                        + "\"name\": \"%s\","
                        + "\"quantity\": %.1f,"
                        + "\"unit\": \"%s\","
                        + "\"categories\": [\"%s\"]"
                        + "},"
                        + "\"updatedSteps\": [\"%s\", \"%s\"],"
                        + "\"macros\": ["
                        + "{ \"type\": \"%s\", \"valuePerUnit\": %.1f }"
                        + "]"
                        + "}",
                ENHANCED_DURATION_MINUTES,
                ENHANCED_INGREDIENT_NAME,
                ENHANCED_INGREDIENT_QUANTITY,
                ENHANCED_INGREDIENT_UNIT,
                ENHANCED_INGREDIENT_CATEGORY,
                ENHANCED_STEP_1,
                ENHANCED_STEP_2,
                MACRO_TYPE,
                MACRO_VALUE_PER_UNIT
        );
    }
}
