package be.xplore.cookbook.rest.controller.recipeController;

import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.request.ImportRecipeRequest;
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

import java.util.Locale;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ImportRecipeTests extends BaseIntegrationTest {

    private static final String IMPORT_RECIPE_TITLE = "Spaghetti Carbonara";
    private static final String IMPORT_RECIPE_DESCRIPTION =
            "Classic Italian pasta dish with eggs, cheese, pancetta, and pepper.";
    private static final int IMPORT_RECIPE_DURATION = 30;
    private static final int IMPORT_RECIPE_SERVINGS = 4;
    private static final String IMPORT_RECIPE_STEP_1 = "Cook pasta according to package instructions.";
    private static final String IMPORT_RECIPE_STEP_2 = "Mix eggs, cheese, and pancetta in a bowl.";
    private static final String IMPORT_RECIPE_INGREDIENT_1_NAME = "Spaghetti";
    private static final Unit IMPORT_RECIPE_INGREDIENT_1_UNIT = Unit.GRAM;
    private static final double IMPORT_RECIPE_INGREDIENT_1_QUANTITY = 400.0;
    private static final String IMPORT_RECIPE_INGREDIENT_1_CATEGORY = "GRAIN";
    private static final String IMPORT_RECIPE_INGREDIENT_2_NAME = "Eggs";
    private static final Unit IMPORT_RECIPE_INGREDIENT_2_UNIT = Unit.PIECE;
    private static final double IMPORT_RECIPE_INGREDIENT_2_QUANTITY = 4.0;
    private static final String IMPORT_RECIPE_INGREDIENT_2_CATEGORY = "EGG";
    private static final String IMPORT_RECIPE_URL = "https://example.com/recipes/spaghetti-carbonara";

    private static WireMockServer wireMockServer;
    private static String mockAiBaseUrl;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        mockAiBaseUrl = "http://localhost:" + wireMockServer.port();
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
        String responseBody = buildOllamaResponseBodyWithContent(buildValidImportResponseContent());
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/api/chat"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));
    }

    @AfterEach
    void resetWireMock() {
        WireMock.reset();
    }

    @Override
    protected String[] getTablesToClear() {
        return new String[]{
                "recipe_ingredients", "recipes", "ingredients",
                "recipe_steps", "ingredient_categories", "users"
        };
    }

    @Test
    void importRecipe_shouldReturnCreatedRecipe_whenValidRequest() throws Exception {
        createUser();
        ImportRecipeRequest request = new ImportRecipeRequest(IMPORT_RECIPE_URL);

        String responseContent = performImportRecipe(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(IMPORT_RECIPE_TITLE)))
                .andExpect(jsonPath("$.description", is(IMPORT_RECIPE_DESCRIPTION)))
                .andExpect(jsonPath("$.durationInMinutes", is(IMPORT_RECIPE_DURATION)))
                .andExpect(jsonPath("$.servings", is(IMPORT_RECIPE_SERVINGS)))
                .andExpect(jsonPath("$.steps", hasSize(2)))
                .andExpect(jsonPath("$.steps[0]", is(IMPORT_RECIPE_STEP_1)))
                .andExpect(jsonPath("$.steps[1]", is(IMPORT_RECIPE_STEP_2)))
                .andExpect(jsonPath("$.ingredients", hasSize(2)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        RecipeDto importedRecipeDto = getMapper().readValue(responseContent, RecipeDto.class);
        assertThat(importedRecipeDto.id()).isNotNull();
        assertThat(getRecipeRepository().findById(new RecipeId(importedRecipeDto.id()), createUser()));
    }

    @Test
    void importRecipe_shouldReturn400_whenUrlIsInvalid() throws Exception {
        ImportRecipeRequest request = new ImportRecipeRequest("invalid-url");
        performImportRecipe(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void importRecipe_shouldReturn401_whenNotAuthenticated() throws Exception {
        ImportRecipeRequest request = new ImportRecipeRequest(IMPORT_RECIPE_URL);
        getMockMvc().perform(post("/api/recipes/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void importRecipe_shouldReturn502_whenAiReturnsInvalidJson() throws Exception {
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/api/chat"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("this is not valid json")));

        createUser();
        ImportRecipeRequest request = new ImportRecipeRequest(IMPORT_RECIPE_URL);
        performImportRecipe(request)
                .andExpect(status().isBadGateway());
    }

    @Test
    void importRecipe_shouldReturn502_whenAiReturnsInvalidStructure() throws Exception {
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/api/chat"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(buildOllamaResponseBodyWithContent("{\"invalid\": \"structure\"}"))));

        createUser();
        ImportRecipeRequest request = new ImportRecipeRequest(IMPORT_RECIPE_URL);
        performImportRecipe(request)
                .andExpect(status().isBadGateway());
    }

    @Test
    void importRecipe_shouldReturn502_whenAiReturnsNullForRequiredField() throws Exception {
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/api/chat"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(buildOllamaResponseBodyWithContent(
                                "{\"title\": null, \"description\": \"test\", \"durationInMinutes\": 30,"
                                        + " \"servings\": 4, \"steps\": [], \"ingredients\": []}"))));

        createUser();
        ImportRecipeRequest request = new ImportRecipeRequest(IMPORT_RECIPE_URL);
        performImportRecipe(request)
                .andExpect(status().isBadGateway());
    }

    @Test
    void importRecipe_shouldReturn503_whenAiUnavailable() throws Exception {
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/api/chat"))
                .willReturn(WireMock.aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        createUser();
        ImportRecipeRequest request = new ImportRecipeRequest(IMPORT_RECIPE_URL);
        performImportRecipe(request)
                .andExpect(status().isServiceUnavailable());
    }

    private ResultActions performImportRecipe(ImportRecipeRequest request) throws Exception {
        return getMockMvc().perform(post("/api/recipes/import")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(request)))
                .andDo(print());
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

    private String buildValidImportResponseContent() {
        return String.format(Locale.US,
                "{"
                        + "\"title\": \"%s\","
                        + "\"description\": \"%s\","
                        + "\"durationInMinutes\": %d,"
                        + "\"servings\": %d,"
                        + "\"steps\": [\"%s\", \"%s\"],"
                        + "\"ingredients\": ["
                        + "{\"name\": \"%s\", \"unit\": \"%s\", \"quantity\": %.1f, \"categories\": [\"%s\"]},"
                        + "{\"name\": \"%s\", \"unit\": \"%s\", \"quantity\": %.1f, \"categories\": [\"%s\"]}"
                        + "]"
                        + "}",
                IMPORT_RECIPE_TITLE,
                IMPORT_RECIPE_DESCRIPTION,
                IMPORT_RECIPE_DURATION,
                IMPORT_RECIPE_SERVINGS,
                IMPORT_RECIPE_STEP_1,
                IMPORT_RECIPE_STEP_2,
                IMPORT_RECIPE_INGREDIENT_1_NAME,
                IMPORT_RECIPE_INGREDIENT_1_UNIT,
                IMPORT_RECIPE_INGREDIENT_1_QUANTITY,
                IMPORT_RECIPE_INGREDIENT_1_CATEGORY,
                IMPORT_RECIPE_INGREDIENT_2_NAME,
                IMPORT_RECIPE_INGREDIENT_2_UNIT,
                IMPORT_RECIPE_INGREDIENT_2_QUANTITY,
                IMPORT_RECIPE_INGREDIENT_2_CATEGORY
        );
    }
}
