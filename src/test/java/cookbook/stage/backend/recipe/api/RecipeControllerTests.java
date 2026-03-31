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
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class RecipeControllerTests {

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

    @Nested
    class CreateRecipeTests {

        @Test
        void createRecipe_shouldReturnRecipe_whenIngredientsExist() throws Exception {
            // Arrange
            Ingredient ingredient1 = new Ingredient(
                    IngredientId.create(),
                    "Ingredient 1 Name",
                    "Ingredient 1 Description",
                    Optional.of(Unit.Milliliter)
            );

            Ingredient ingredient2 = new Ingredient(
                    IngredientId.create(),
                    "Ingredient 2 Name",
                    "Ingredient 2 Description",
                    Optional.of(Unit.Gram)
            );

            ingredientRepository.save(ingredient1);
            ingredientRepository.save(ingredient2);

            boolean scalable1 = true;
            double quantity1 = 1;
            boolean scalable2 = true;
            double quantity2 = 1;

            String name = "Test Name";
            String description = "Test Description";
            int duration = 60;
            List<String> steps = List.of("This is step 1", "This is step 2");
            List<RecipeIngredientDto> ingredients = List.of(
                    new RecipeIngredientDto(ingredient1.id().id(), scalable1, quantity1),
                    new RecipeIngredientDto(ingredient2.id().id(), scalable2, quantity2));

            CreateRecipeDto dto = new CreateRecipeDto(
                    name,
                    description,
                    duration,
                    steps,
                    ingredients
            );

            // Act & Assert
            mockMvc.perform(post("/api/v1/recipes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                    // Verify an ID was generated and returned
                    .andExpect(jsonPath("$.id").exists())

                    // Verify basic recipe details
                    .andExpect(jsonPath("$.name").value(name))
                    .andExpect(jsonPath("$.description").value(description))
                    .andExpect(jsonPath("$.durationInMinutes").value(duration))
                    .andExpect(jsonPath("$.steps", hasSize(2)))
                    .andExpect(jsonPath("$.steps[0]").value(steps.get(0)))
                    .andExpect(jsonPath("$.steps[1]").value(steps.get(1)))

                    // Verify ingredients
                    .andExpect(jsonPath("$.ingredients", hasSize(2)))
                    .andExpect(jsonPath("$.ingredients[0].ingredientId").value(ingredient1.id().id().toString()))
                    .andExpect(jsonPath("$.ingredients[0].isScalable").value(scalable1))
                    .andExpect(jsonPath("$.ingredients[0].baseQuantity").value(quantity1))
                    .andExpect(jsonPath("$.ingredients[1].ingredientId").value(ingredient2.id().id().toString()))
                    .andExpect(jsonPath("$.ingredients[1].isScalable").value(scalable2))
                    .andExpect(jsonPath("$.ingredients[1].baseQuantity").value(quantity2))
                    .andReturn();

            assertThat(recipeRepository.findAll(PageRequest.of(0, 20))).hasSize(1);
        }

        @Test
        void createRecipe_shouldReturn404_whenIngredientsNotExist() throws Exception {

        }

        @Test
        void createRecipe_shouldReturn400_whenDtoInvalid() throws Exception {

        }
    }
}
