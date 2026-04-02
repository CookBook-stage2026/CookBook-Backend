package cookbook.stage.backend.recipe.api.recipeController;

import cookbook.stage.backend.recipe.domain.Ingredient;
import cookbook.stage.backend.recipe.domain.Recipe;
import cookbook.stage.backend.recipe.domain.RecipeRepository;
import cookbook.stage.backend.recipe.shared.RecipeId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class GetAllRecipesTests {

    private static final String DEFAULT_RECIPE_NAME = "Test Name";
    private static final String DEFAULT_RECIPE_DESCRIPTION = "Test Description";
    private static final int DEFAULT_DURATION_IN_MINUTES = 60;
    private static final List<String> DEFAULT_STEPS = List.of("This is step 1", "This is step 2");
    private static final double DEFAULT_QUANTITY = 1.0;
    private static final String DEFAULT_UNIT = "gram";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecipeRepository recipeRepository;

    @AfterEach
    void tearDown() {
        recipeRepository.deleteAll();
    }

    @Test
    void getAllRecipes_shouldReturnAllRecipes_whenRecipesExist() throws Exception {
        // Arrange
        Recipe recipe1 = recipeRepository.save(buildRecipe());
        Recipe recipe2 = recipeRepository.save(buildRecipe());

        // Act & Assert
        performGetAllRecipes()
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value(DEFAULT_RECIPE_NAME))
                .andExpect(jsonPath("$[0].description").value(DEFAULT_RECIPE_DESCRIPTION))
                .andExpect(jsonPath("$[0].durationInMinutes").value(DEFAULT_DURATION_IN_MINUTES))
                .andExpect(jsonPath("$[0].id").value(recipe1.getId().id().toString()))
                .andExpect(jsonPath("$[1].id").value(recipe2.getId().id().toString()));
    }

    @Test
    void getAllRecipes_shouldReturnEmptyList_whenNoRecipesExist() throws Exception {
        performGetAllRecipes()
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllRecipes_shouldReturnPagedResults_whenPageSizeIsSmall() throws Exception {
        // Arrange
        recipeRepository.save(buildRecipe());
        recipeRepository.save(buildRecipe());
        recipeRepository.save(buildRecipe());

        // Act & Assert
        performGetAllRecipes(0, 2)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        performGetAllRecipes(1, 2)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    private Recipe buildRecipe() {
        return new Recipe(
                RecipeId.create(),
                DEFAULT_RECIPE_NAME,
                DEFAULT_RECIPE_DESCRIPTION,
                DEFAULT_DURATION_IN_MINUTES,
                DEFAULT_STEPS,
                List.of(new Ingredient("Flour", DEFAULT_QUANTITY, DEFAULT_UNIT))
        );
    }

    private ResultActions performGetAllRecipes() throws Exception {
        return mockMvc.perform(get("/api/recipes"))
                .andDo(print());
    }

    private ResultActions performGetAllRecipes(int page, int size) throws Exception {
        return mockMvc.perform(get("/api/recipes")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andDo(print());
    }
}
