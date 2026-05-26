package be.xplore.cookbook.rest.controller.recipe.recipeController;

import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.recipe.request.NewRecipeIngredientDto;
import be.xplore.cookbook.rest.dto.recipe.request.UpdateRecipeDto;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdateRecipeTests extends BaseIntegrationTest {

    private static final double DEFAULT_QUANTITY = 1.0;
    private static final int MINUTES_IN_HOUR = 60;

    private static final String UPDATED_NAME = "Updated Name";
    private static final String UPDATED_DESCRIPTION = "Updated Description";
    private static final String UPDATED_STEP_1 = "Updated step 1";
    private static final String UPDATED_STEP_2 = "Updated step 2";
    private static final int UPDATED_SERVINGS = 4;

    private static final double TOTAL_CALORIES = 720.0;
    private static final double TOTAL_FAT = 2.0;

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
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/api/chat"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(buildOllamaResponseBody(buildMacrosResponseContent()))));
    }

    @AfterEach
    void resetWireMock() {
        WireMock.reset();
    }

    @Override
    protected String[] getTablesToClear() {
        return new String[]{
                "recipe_ingredients",
                "recipe_steps",
                "recipes",
                "recipe_macros",
                "ingredients",
                "users"
        };
    }

    @Test
    void updateRecipe_shouldReturn204_whenRequestIsValid() throws Exception {
        User user = createUser();
        Ingredient flour = createAndSaveIngredient("Flour");
        Recipe recipe = createAndSaveRecipe(user);

        UpdateRecipeDto dto = buildUpdateRecipeDto(List.of(
                new NewRecipeIngredientDto(flour.id().id(), DEFAULT_QUANTITY)
        ));

        performUpdateRecipe(recipe.getId().id().toString(), dto)
                .andExpect(status().isNoContent());

        Recipe updatedRecipe = getRecipeRepository()
                .findById(recipe.getId(), user)
                .orElseThrow();

        assertThat(updatedRecipe.getName()).isEqualTo(UPDATED_NAME);
        assertThat(updatedRecipe.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(updatedRecipe.getDurationInMinutes()).isEqualTo(MINUTES_IN_HOUR);
        assertThat(updatedRecipe.getSteps()).containsExactly(UPDATED_STEP_1, UPDATED_STEP_2);
        assertThat(updatedRecipe.getServings()).isEqualTo(UPDATED_SERVINGS);
        assertThat(updatedRecipe.getIngredients()).hasSize(1);
    }

    @Test
    void updateRecipe_shouldReturn400_whenIngredientDoesNotExist() throws Exception {
        User user = createUser();
        Recipe recipe = createAndSaveRecipe(user);

        UpdateRecipeDto dto = buildUpdateRecipeDto(List.of(
                new NewRecipeIngredientDto(UUID.randomUUID(), DEFAULT_QUANTITY)
        ));

        performUpdateRecipe(recipe.getId().id().toString(), dto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRecipe_shouldReturn404_whenRecipeNotFound() throws Exception {
        createUser();
        Ingredient ingredient = createAndSaveIngredient("Flour");

        UpdateRecipeDto dto = buildUpdateRecipeDto(List.of(
                new NewRecipeIngredientDto(ingredient.id().id(), DEFAULT_QUANTITY)
        ));

        performUpdateRecipe(UUID.randomUUID().toString(), dto)
                .andExpect(status().isNotFound());
    }

    @Test
    void updateRecipe_shouldReturn404_whenRecipeBelongsToOtherUser() throws Exception {
        User owner = createUserWithId(UserId.create());
        Recipe recipe = createAndSaveRecipe(owner);
        Ingredient flour = createAndSaveIngredient("Flour");

        createUser();

        UpdateRecipeDto dto = buildUpdateRecipeDto(List.of(
                new NewRecipeIngredientDto(flour.id().id(), DEFAULT_QUANTITY)
        ));

        performUpdateRecipe(recipe.getId().id().toString(), dto)
                .andExpect(status().isNotFound());
    }

    @Test
    void updateRecipe_shouldReturn401_whenNotAuthenticated() throws Exception {
        Ingredient flour = createAndSaveIngredient("Flour");

        UpdateRecipeDto dto = buildUpdateRecipeDto(List.of(
                new NewRecipeIngredientDto(flour.id().id(), DEFAULT_QUANTITY)
        ));

        getMockMvc().perform(put("/api/recipes/{id}", UUID.randomUUID())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    private UpdateRecipeDto buildUpdateRecipeDto(List<NewRecipeIngredientDto> ingredients) {
        return new UpdateRecipeDto(
                UPDATED_NAME,
                UPDATED_DESCRIPTION,
                MINUTES_IN_HOUR,
                List.of(UPDATED_STEP_1, UPDATED_STEP_2),
                ingredients,
                UPDATED_SERVINGS,
                true
        );
    }

    private ResultActions performUpdateRecipe(String recipeId, UpdateRecipeDto dto) throws Exception {
        return getMockMvc().perform(put("/api/recipes/{id}", recipeId)
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
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

    private String buildMacrosResponseContent() {
        return String.format(Locale.US,
                """
                        {
                            "macros": [
                                {
                                    "type": "CALORIES",
                                    "valuePerUnit": %.1f
                                },
                                {
                                    "type": "FAT",
                                    "valuePerUnit": %.1f
                                }
                            ]
                        }
                        """,
                TOTAL_CALORIES,
                TOTAL_FAT
        );
    }
}
