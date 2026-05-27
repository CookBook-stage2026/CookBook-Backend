package be.xplore.cookbook.rest.controller.recipe.recipeController;

import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.MacroType;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.recipe.request.CreateRecipeDto;
import be.xplore.cookbook.rest.dto.recipe.request.NewRecipeIngredientDto;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateRecipeTests extends BaseIntegrationTest {

    private static final String TEST_RECIPE_NAME = "Test Name";
    private static final String TEST_RECIPE_DESCRIPTION = "Test Description";
    private static final int TEST_RECIPE_DURATION_MINUTES = 60;
    private static final int TEST_RECIPE_SERVINGS = 2;
    private static final boolean TEST_RECIPE_IS_PUBLIC = true;
    private static final String TEST_RECIPE_STEP_1 = "This is step 1";
    private static final String TEST_RECIPE_STEP_2 = "This is step 2";

    private static final String INGREDIENT_FLOUR_NAME = "Flour";
    private static final String INGREDIENT_EGGS_NAME = "Eggs";
    private static final double INGREDIENT_DEFAULT_QUANTITY = 1.0;
    private static final double INGREDIENT_EGGS_QUANTITY = 2.0;
    private static final double INGREDIENT_FLOUR_QUANTITY = 200.0;

    private static final double TOTAL_CALORIES = 720.0;
    private static final double TOTAL_FAT = 2.0;

    private static final int EXPECTED_MACRO_COUNT_TWO = 2;
    private static final int EXPECTED_INGREDIENT_COUNT_TWO = 2;
    private static final int EXPECTED_RECIPE_COUNT_ONE = 1;

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
                "recipe_ingredients",
                "recipe_steps",
                "recipes",
                "recipe_macros",
                "ingredients",
                "users"
        };
    }

    @Test
    void createRecipe_shouldReturnRecipe_whenRequestIsValid() throws Exception {
        // Arrange
        Ingredient flour = createAndSaveIngredient(INGREDIENT_FLOUR_NAME);
        Ingredient eggs = createAndSaveIngredient(INGREDIENT_EGGS_NAME);

        CreateRecipeDto dto = buildCreateRecipeDto(List.of(
                new NewRecipeIngredientDto(flour.id().id(), INGREDIENT_DEFAULT_QUANTITY),
                new NewRecipeIngredientDto(eggs.id().id(), INGREDIENT_EGGS_QUANTITY)
        ));

        User user = createUser();

        // Act & Assert
        MvcResult result = performCreateRecipeWithValidJwt(dto)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(TEST_RECIPE_NAME))
                .andExpect(jsonPath("$.description").value(TEST_RECIPE_DESCRIPTION))
                .andExpect(jsonPath("$.durationInMinutes").value(TEST_RECIPE_DURATION_MINUTES))
                .andExpect(jsonPath("$.servings").value(TEST_RECIPE_SERVINGS))
                .andExpect(jsonPath("$.steps[0]").value(TEST_RECIPE_STEP_1))
                .andExpect(jsonPath("$.steps[1]").value(TEST_RECIPE_STEP_2))
                .andExpect(jsonPath("$.ingredients", hasSize(EXPECTED_INGREDIENT_COUNT_TWO)))
                .andExpect(jsonPath("$.ingredients[*].ingredientId", hasItems(
                        flour.id().id().toString(),
                        eggs.id().id().toString()
                )))
                .andExpect(jsonPath("$.ingredients[*].quantity", hasItems(
                        INGREDIENT_DEFAULT_QUANTITY,
                        INGREDIENT_EGGS_QUANTITY
                )))
                .andExpect(jsonPath("$.totalMacros", hasSize(EXPECTED_MACRO_COUNT_TWO)))
                .andReturn();

        RecipeDto response = getMapper().readValue(
                result.getResponse().getContentAsString(),
                RecipeDto.class
        );

        RecipeId recipeId = new RecipeId(response.id());

        getRecipeRepository().findById(recipeId, user)
                .orElseThrow(() -> new Exception("Recipe with id " + recipeId + " not found!"));

        assertThat(getRecipeRepository().count()).isEqualTo(EXPECTED_RECIPE_COUNT_ONE);
    }

    @Test
    void createRecipe_shouldReturnTotalMacros_whenIngredientsHaveMacros() throws Exception {
        // Arrange
        Ingredient flour = createAndSaveIngredient(INGREDIENT_FLOUR_NAME);

        CreateRecipeDto dto = buildCreateRecipeDto(List.of(
                new NewRecipeIngredientDto(flour.id().id(), INGREDIENT_FLOUR_QUANTITY)
        ));

        createUser();

        // Act & Assert
        performCreateRecipeWithValidJwt(dto)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalMacros", hasSize(EXPECTED_MACRO_COUNT_TWO)))
                .andExpect(jsonPath("$.totalMacros[0].type").value("CALORIES"))
                .andExpect(jsonPath("$.totalMacros[0].value").value(TOTAL_CALORIES))
                .andExpect(jsonPath("$.totalMacros[1].type").value("FAT"))
                .andExpect(jsonPath("$.totalMacros[1].value").value(TOTAL_FAT));
    }

    @Test
    void createRecipe_shouldReturn400_whenRequestInvalid() throws Exception {
        // Arrange
        CreateRecipeDto dto = new CreateRecipeDto(
                null,
                null,
                TEST_RECIPE_DURATION_MINUTES,
                List.of(TEST_RECIPE_STEP_1, TEST_RECIPE_STEP_2),
                List.of(),
                TEST_RECIPE_IS_PUBLIC,
                TEST_RECIPE_SERVINGS
        );

        createUser();

        // Act & Assert
        performCreateRecipeWithValidJwt(dto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRecipe_shouldReturn400_whenQuantityIsNegative() throws Exception {
        // Arrange
        Ingredient flour = createAndSaveIngredient(INGREDIENT_FLOUR_NAME);
        double negativeQuantity = -1.0;

        CreateRecipeDto dto = new CreateRecipeDto(
                TEST_RECIPE_NAME,
                TEST_RECIPE_DESCRIPTION,
                TEST_RECIPE_DURATION_MINUTES,
                List.of(TEST_RECIPE_STEP_1, TEST_RECIPE_STEP_2),
                List.of(new NewRecipeIngredientDto(flour.id().id(), negativeQuantity)),
                TEST_RECIPE_IS_PUBLIC,
                TEST_RECIPE_SERVINGS
        );

        createUser();

        // Act & Assert
        performCreateRecipeWithValidJwt(dto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRecipe_shouldReturn400_whenIngredientDoesNotExist() throws Exception {
        // Arrange
        CreateRecipeDto dto = buildCreateRecipeDto(List.of(
                new NewRecipeIngredientDto(UUID.randomUUID(), INGREDIENT_DEFAULT_QUANTITY)
        ));

        createUser();

        // Act & Assert
        performCreateRecipeWithValidJwt(dto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRecipe_shouldReturn401_whenNotAuthenticated() throws Exception {
        // Arrange
        Ingredient flour = createAndSaveIngredient(INGREDIENT_FLOUR_NAME);

        CreateRecipeDto dto = buildCreateRecipeDto(List.of(
                new NewRecipeIngredientDto(flour.id().id(), INGREDIENT_DEFAULT_QUANTITY)
        ));

        // Act & Assert
        getMockMvc().perform(post("/api/recipes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createRecipe_shouldReturn503_whenAiUnavailableForMacros() throws Exception {
        // Arrange
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/api/chat"))
                .willReturn(WireMock.aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        Ingredient flour = createAndSaveIngredient(INGREDIENT_FLOUR_NAME);
        CreateRecipeDto dto = buildCreateRecipeDto(List.of(
                new NewRecipeIngredientDto(flour.id().id(), INGREDIENT_DEFAULT_QUANTITY)
        ));
        createUser();

        // Act & Assert
        performCreateRecipeWithValidJwt(dto)
                .andExpect(status().isServiceUnavailable());
    }

    private CreateRecipeDto buildCreateRecipeDto(List<NewRecipeIngredientDto> ingredients) {
        return new CreateRecipeDto(
                TEST_RECIPE_NAME,
                TEST_RECIPE_DESCRIPTION,
                TEST_RECIPE_DURATION_MINUTES,
                List.of(TEST_RECIPE_STEP_1, TEST_RECIPE_STEP_2),
                ingredients,
                TEST_RECIPE_IS_PUBLIC,
                TEST_RECIPE_SERVINGS
        );
    }

    private ResultActions performCreateRecipeWithValidJwt(CreateRecipeDto dto) throws Exception {
        return getMockMvc().perform(post("/api/recipes")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
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
