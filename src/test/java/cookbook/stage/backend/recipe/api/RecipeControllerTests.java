package cookbook.stage.backend.recipe.api;

import cookbook.stage.backend.ingredient.domain.Ingredient;
import cookbook.stage.backend.ingredient.domain.IngredientRepository;
import cookbook.stage.backend.ingredient.domain.Unit;
import cookbook.stage.backend.ingredient.shared.IngredientId;
import cookbook.stage.backend.recipe.api.dto.CreateRecipeDto;
import cookbook.stage.backend.recipe.api.dto.RecipeIngredientDto;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class RecipeControllerTests {

    private static final String DEFAULT_RECIPE_NAME = "Test Name";
    private static final String DEFAULT_RECIPE_DESCRIPTION = "Test Description";
    private static final int DEFAULT_DURATION_IN_MINUTES = 60;
    private static final List<String> DEFAULT_STEPS = List.of("This is step 1", "This is step 2");
    private static final boolean DEFAULT_SCALABLE = true;
    private static final double DEFAULT_QUANTITY = 1.0;
    private static final int PAGE_SIZE = 20;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @AfterEach
    void tearDown() {
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
    }

    private Ingredient createAndSaveIngredient(String name, String description, Unit unit) {
        Ingredient ingredient = new Ingredient(
                IngredientId.create(),
                name,
                description,
                Optional.of(unit)
        );
        ingredientRepository.save(ingredient);
        return ingredient;
    }

    private CreateRecipeDto buildCreateRecipeDto(List<RecipeIngredientDto> ingredients) {
        return new CreateRecipeDto(
                DEFAULT_RECIPE_NAME,
                DEFAULT_RECIPE_DESCRIPTION,
                DEFAULT_DURATION_IN_MINUTES,
                DEFAULT_STEPS,
                ingredients
        );
    }

    private RecipeIngredientDto ingredientDto(UUID ingredientId) {
        return new RecipeIngredientDto(ingredientId, DEFAULT_SCALABLE, DEFAULT_QUANTITY);
    }

    private ResultActions performCreateRecipe(CreateRecipeDto dto) throws Exception {
        return mockMvc.perform(post("/api/v1/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }

    @Nested
    class CreateRecipeTests {

        @Test
        void createRecipe_shouldReturnRecipe_whenIngredientsExist() throws Exception {
            Ingredient ingredient1 = createAndSaveIngredient(
                    "Ingredient 1 Name",
                    "Ingredient 1 Description",
                    Unit.Milliliter);
            Ingredient ingredient2 = createAndSaveIngredient(
                    "Ingredient 2 Name",
                    "Ingredient 2 Description",
                    Unit.Gram);

            CreateRecipeDto dto = buildCreateRecipeDto(List.of(
                    ingredientDto(ingredient1.id().id()),
                    ingredientDto(ingredient2.id().id())
            ));

            performCreateRecipe(dto)
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").value(DEFAULT_RECIPE_NAME))
                    .andExpect(jsonPath("$.description").value(DEFAULT_RECIPE_DESCRIPTION))
                    .andExpect(jsonPath("$.durationInMinutes").value(DEFAULT_DURATION_IN_MINUTES))
                    .andExpect(jsonPath("$.steps", hasSize(DEFAULT_STEPS.size())))
                    .andExpect(jsonPath("$.steps[0]").value(DEFAULT_STEPS.get(0)))
                    .andExpect(jsonPath("$.steps[1]").value(DEFAULT_STEPS.get(1)))
                    .andExpect(jsonPath("$.ingredients", hasSize(2)))
                    .andExpect(jsonPath("$.ingredients[0].ingredientId").value(ingredient1.id().id().toString()))
                    .andExpect(jsonPath("$.ingredients[0].isScalable").value(DEFAULT_SCALABLE))
                    .andExpect(jsonPath("$.ingredients[0].baseQuantity").value(DEFAULT_QUANTITY))
                    .andExpect(jsonPath("$.ingredients[1].ingredientId").value(ingredient2.id().id().toString()))
                    .andExpect(jsonPath("$.ingredients[1].isScalable").value(DEFAULT_SCALABLE))
                    .andExpect(jsonPath("$.ingredients[1].baseQuantity").value(DEFAULT_QUANTITY));

            assertThat(recipeRepository.findAll(PageRequest.of(0, PAGE_SIZE))).hasSize(1);
        }

        @Test
        void createRecipe_shouldReturn404_whenIngredientsNotExist() throws Exception {
            CreateRecipeDto dto = buildCreateRecipeDto(List.of(
                    ingredientDto(UUID.randomUUID())
            ));

            performCreateRecipe(dto)
                    .andExpect(status().isNotFound());
        }

        @Test
        void createRecipe_shouldReturn400_whenDtoInvalid() throws Exception {
            CreateRecipeDto dto = new CreateRecipeDto(
                    null,
                    DEFAULT_RECIPE_DESCRIPTION,
                    DEFAULT_DURATION_IN_MINUTES,
                    DEFAULT_STEPS,
                    List.of()
            );

            performCreateRecipe(dto)
                    .andExpect(status().isBadRequest());
        }
    }
}
