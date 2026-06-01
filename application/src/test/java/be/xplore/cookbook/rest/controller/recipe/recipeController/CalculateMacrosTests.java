package be.xplore.cookbook.rest.controller.recipe.recipeController;

import be.xplore.cookbook.core.domain.ingredient.MacroType;
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

import java.util.Locale;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CalculateMacrosTests extends BaseIntegrationTest {

    private static final double TOTAL_CALORIES = 720.0;
    private static final double TOTAL_FAT = 2.0;

    private static final String OLLAMA_CHAT_PATH = "/api/chat";

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
        String responseBody = buildOllamaResponseBodyWithContent(buildMacrosResponseContent());

        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching(OLLAMA_CHAT_PATH))
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
                "recipe_macros",
                "recipe_ingredients",
                "recipe_steps",
                "recipes",
                "ingredients",
                "users"
        };
    }

    @Test
    void calculateMacros_shouldReturnRecipeWithMacros_whenRequestIsValid() throws Exception {
        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);

        String result = performCalculateMacros(recipe.getId().id().toString())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(recipe.getId().id().toString()))
                .andExpect(jsonPath("$.totalMacros").isArray())
                .andExpect(jsonPath("$.totalMacros[0].type").value(MacroType.CALORIES.name()))
                .andExpect(jsonPath("$.totalMacros[0].value").value(TOTAL_CALORIES))
                .andExpect(jsonPath("$.totalMacros[1].type").value(MacroType.FAT.name()))
                .andExpect(jsonPath("$.totalMacros[1].value").value(TOTAL_FAT))
                .andReturn()
                .getResponse()
                .getContentAsString();

        RecipeDto dto = getMapper().readValue(result, RecipeDto.class);
        Recipe saved = getRecipeRepository().findOwnById(recipe.getId(), user).orElseThrow();

        assertThat(saved.getMacros()).hasSize(2);
        assertThat(dto.totalMacros()).hasSize(2);
    }

    @Test
    void calculateMacros_shouldReturn404_whenRecipeNotFound() throws Exception {
        createUser();

        performCalculateMacros(UUID.randomUUID().toString())
                .andExpect(status().isNotFound());
    }

    @Test
    void calculateMacros_shouldReturn404_whenRecipeBelongsToOtherUser() throws Exception {
        User owner = createUserWithId(UserId.create());
        Recipe recipe = createAndSaveRecipe(owner);

        createUser();

        performCalculateMacros(recipe.getId().id().toString())
                .andExpect(status().isNotFound());
    }

    @Test
    void calculateMacros_shouldReturn401_whenNotAuthenticated() throws Exception {
        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);

        getMockMvc().perform(post("/api/recipes/{id}/macros", recipe.getId().id().toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void calculateMacros_shouldReturn503_whenAiUnavailable() throws Exception {
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching(OLLAMA_CHAT_PATH))
                .willReturn(WireMock.aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);

        performCalculateMacros(recipe.getId().id().toString())
                .andExpect(status().isServiceUnavailable());
    }

    private ResultActions performCalculateMacros(String recipeId) throws Exception {
        return getMockMvc().perform(post("/api/recipes/{id}/macros", recipeId)
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    private String buildOllamaResponseBodyWithContent(String content) {
        record Message(String role, String content) {
        }

        record OllamaResponse(Message message, boolean done) {
        }

        return getMapper().writeValueAsString(
                new OllamaResponse(
                        new Message("assistant", content),
                        true
                )
        );
    }

    private String buildMacrosResponseContent() {
        return String.format(Locale.US,
                """
                        {
                            "macros": [
                                {
                                    "type": "%s",
                                    "valuePerUnit": %.1f
                                },
                                {
                                    "type": "%s",
                                    "valuePerUnit": %.1f
                                }
                            ]
                        }
                        """,
                MacroType.CALORIES,
                TOTAL_CALORIES,
                MacroType.FAT,
                TOTAL_FAT
        );
    }
}
