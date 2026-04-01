package cookbook.stage.backend.recipe.api;

import cookbook.stage.backend.recipe.api.dto.CreateRecipeDto;
import cookbook.stage.backend.recipe.api.dto.IngredientDto;
import cookbook.stage.backend.recipe.domain.RecipeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class RecipeControllerTests {

    private static final String DEFAULT_RECIPE_NAME = "Test Name";
    private static final String DEFAULT_RECIPE_DESCRIPTION = "Test Description";
    private static final int DEFAULT_DURATION_IN_MINUTES = 60;
    private static final List<String> DEFAULT_STEPS = List.of("This is step 1", "This is step 2");
    private static final double DEFAULT_QUANTITY = 1.0;
    private static final String DEFAULT_UNIT = "gram";
    private static final int PAGE_SIZE = 20;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecipeRepository recipeRepository;

    @AfterEach
    void tearDown() {
        recipeRepository.deleteAll();
    }

    private Map<String, IngredientDto> defaultIngredients() {
        return Map.of(
                "Flour", new IngredientDto("Flour", DEFAULT_QUANTITY, DEFAULT_UNIT),
                "Eggs", new IngredientDto("Eggs", 2.0, null)
        );
    }

    private CreateRecipeDto buildCreateRecipeDto(Map<String, IngredientDto> ingredients) {
        return new CreateRecipeDto(
                DEFAULT_RECIPE_NAME,
                DEFAULT_RECIPE_DESCRIPTION,
                DEFAULT_DURATION_IN_MINUTES,
                DEFAULT_STEPS,
                ingredients
        );
    }

    private ResultActions performCreateRecipe(CreateRecipeDto dto) throws Exception {
        return mockMvc.perform(post("/api/v1/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)));
    }

    @Nested
    class CreateRecipeTests {

        @Test
        void createRecipe_shouldReturnRecipe_whenRequestIsValid() throws Exception {
            // Arrange
            CreateRecipeDto dto = buildCreateRecipeDto(defaultIngredients());

            // Act & Assert
            performCreateRecipe(dto)
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").value(DEFAULT_RECIPE_NAME))
                    .andExpect(jsonPath("$.description").value(DEFAULT_RECIPE_DESCRIPTION))
                    .andExpect(jsonPath("$.durationInMinutes").value(DEFAULT_DURATION_IN_MINUTES))
                    .andExpect(jsonPath("$.steps[0]").value(DEFAULT_STEPS.get(0)))
                    .andExpect(jsonPath("$.steps[1]").value(DEFAULT_STEPS.get(1)))
                    .andExpect(jsonPath("$.ingredients.Flour.quantity").value(DEFAULT_QUANTITY))
                    .andExpect(jsonPath("$.ingredients.Flour.unit").value(DEFAULT_UNIT))
                    .andExpect(jsonPath("$.ingredients.Eggs.quantity").value(2.0))
                    .andExpect(jsonPath("$.ingredients.Eggs.unit").doesNotExist());

            assertThat(recipeRepository.findAll(PageRequest.of(0, PAGE_SIZE))).hasSize(1);
        }

        @Test
        void createRecipe_shouldReturn400_whenRequestInvalid() throws Exception {
            // Arrange
            CreateRecipeDto dto = new CreateRecipeDto(
                    null,
                    DEFAULT_RECIPE_DESCRIPTION,
                    DEFAULT_DURATION_IN_MINUTES,
                    DEFAULT_STEPS,
                    defaultIngredients()
            );

            // Act & Assert
            performCreateRecipe(dto)
                    .andExpect(status().isBadRequest());
        }
    }
}
