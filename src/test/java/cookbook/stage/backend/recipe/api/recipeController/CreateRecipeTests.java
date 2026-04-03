package cookbook.stage.backend.recipe.api.recipeController;

import cookbook.stage.backend.ingredient.domain.Ingredient;
import cookbook.stage.backend.ingredient.domain.IngredientRepository;
import cookbook.stage.backend.ingredient.domain.Unit;
import cookbook.stage.backend.ingredient.shared.IngredientId;
import cookbook.stage.backend.recipe.api.dto.CreateRecipeDto;
import cookbook.stage.backend.recipe.api.dto.CreateRecipeIngredientDto;
import cookbook.stage.backend.recipe.domain.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class CreateRecipeTests {

    private static final String DEFAULT_RECIPE_NAME = "Test Name";
    private static final String DEFAULT_RECIPE_DESCRIPTION = "Test Description";
    private static final int DEFAULT_DURATION_IN_MINUTES = 60;
    private static final int DEFAULT_SERVINGS = 2;
    private static final List<String> DEFAULT_STEPS = List.of("This is step 1", "This is step 2");
    private static final double DEFAULT_QUANTITY = 1.0;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @BeforeEach
    void tearDown() {
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
    }

    @Test
    void createRecipe_shouldReturnRecipe_whenRequestIsValid() throws Exception {
        // Arrange
        Ingredient flour = ingredientRepository.save(new Ingredient(new IngredientId(UUID.randomUUID()), "Flour", Unit.GRAM));
        Ingredient eggs = ingredientRepository.save(new Ingredient(new IngredientId(UUID.randomUUID()), "Eggs", Unit.PIECE));

        CreateRecipeDto dto = buildCreateRecipeDto(List.of(
                new CreateRecipeIngredientDto(flour.id().id(), DEFAULT_QUANTITY),
                new CreateRecipeIngredientDto(eggs.id().id(), 2.0)
        ));

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
                .andExpect(jsonPath("$.ingredients", hasSize(2)))
                .andExpect(jsonPath("$.ingredients[0].ingredientId").value(flour.id().id().toString()))
                .andExpect(jsonPath("$.ingredients[0].baseQuantity").value(DEFAULT_QUANTITY))
                .andExpect(jsonPath("$.ingredients[1].ingredientId").value(eggs.id().id().toString()))
                .andExpect(jsonPath("$.ingredients[1].baseQuantity").value(2.0));

        assertThat(recipeRepository.count()).isEqualTo(1);
    }

    @Test
    void createRecipe_shouldReturn400_whenRequestInvalid() throws Exception {
        // Arrange
        CreateRecipeDto dto = new CreateRecipeDto(
                null,
                null,
                DEFAULT_DURATION_IN_MINUTES,
                DEFAULT_STEPS,
                List.of(),
                DEFAULT_SERVINGS
        );

        // Act & Assert
        performCreateRecipe(dto)
                .andExpect(status().isBadRequest());
    }

    private CreateRecipeDto buildCreateRecipeDto(List<CreateRecipeIngredientDto> ingredients) {
        return new CreateRecipeDto(
                DEFAULT_RECIPE_NAME,
                DEFAULT_RECIPE_DESCRIPTION,
                DEFAULT_DURATION_IN_MINUTES,
                DEFAULT_STEPS,
                ingredients,
                DEFAULT_SERVINGS
        );
    }

    private ResultActions performCreateRecipe(CreateRecipeDto dto) throws Exception {
        return mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print());
    }
}