package cookbook.stage.backend.recipe.api.recipeController;

import cookbook.stage.backend.ingredient.domain.Ingredient;
import cookbook.stage.backend.ingredient.domain.IngredientRepository;
import cookbook.stage.backend.ingredient.domain.Unit;
import cookbook.stage.backend.ingredient.shared.IngredientId;
import cookbook.stage.backend.recipe.domain.Recipe;
import cookbook.stage.backend.recipe.domain.RecipeIngredient;
import cookbook.stage.backend.recipe.domain.RecipeRepository;
import cookbook.stage.backend.recipe.shared.RecipeId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

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
    private static final int DEFAULT_SERVINGS = 2;
    private static final List<String> DEFAULT_STEPS = List.of("This is step 1", "This is step 2");
    private static final double DEFAULT_QUANTITY = 1.0;

    @Autowired
    private MockMvc mockMvc;

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
    void getAllRecipes_shouldReturnAllRecipes_whenRecipesExist() throws Exception {
        // Arrange
        Recipe recipe1 = recipeRepository.save(buildRecipe());
        Recipe recipe2 = recipeRepository.save(buildRecipe());

        // Act & Assert
        performGetAllRecipes()
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value(DEFAULT_RECIPE_NAME))
                .andExpect(jsonPath("$.content[0].description").value(DEFAULT_RECIPE_DESCRIPTION))
                .andExpect(jsonPath("$.content[0].durationInMinutes").value(DEFAULT_DURATION_IN_MINUTES))
                .andExpect(jsonPath("$.content[0].id").value(recipe1.getId().id().toString()))
                .andExpect(jsonPath("$.content[1].id").value(recipe2.getId().id().toString()))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    void getAllRecipes_shouldReturnEmptyList_whenNoRecipesExist() throws Exception {
        performGetAllRecipes()
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.page.totalPages").value(0));
    }

    @Test
    void getAllRecipes_shouldReturnPagedResults_whenPageSizeIsSmall() throws Exception {
        // Arrange
        final int totalRecipes = 3;
        final int pageSize = 2;
        final int firstPageIndex = 0;
        final int secondPageIndex = 1;

        final int expectedTotalPages = (int) Math.ceil((double) totalRecipes / pageSize);
        final int expectedFirstPageCount = Math.min(totalRecipes, pageSize);
        final int expectedSecondPageCount = totalRecipes - pageSize;

        for (int i = 0; i < totalRecipes; i++) {
            recipeRepository.save(buildRecipe());
        }

        long totalElements = recipeRepository.count();

        // Act & Assert
        performGetAllRecipes(firstPageIndex, pageSize)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(expectedFirstPageCount)))
                .andExpect(jsonPath("$.page.totalElements").value(totalElements))
                .andExpect(jsonPath("$.page.totalPages").value(expectedTotalPages))
                .andExpect(jsonPath("$.page.number").value(firstPageIndex))
                .andExpect(jsonPath("$.page.size").value(pageSize));

        // Act & Assert
        performGetAllRecipes(secondPageIndex, pageSize)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(expectedSecondPageCount)))
                .andExpect(jsonPath("$.page.number").value(secondPageIndex))
                .andExpect(jsonPath("$.page.size").value(pageSize));
    }

    private Recipe buildRecipe() {
        Ingredient ingredient = new Ingredient(new IngredientId(UUID.randomUUID()), "Flour " + UUID.randomUUID(), Unit.GRAM);
        ingredientRepository.save(ingredient);

        return new Recipe(
                RecipeId.create(),
                DEFAULT_RECIPE_NAME,
                DEFAULT_RECIPE_DESCRIPTION,
                DEFAULT_DURATION_IN_MINUTES,
                DEFAULT_STEPS,
                List.of(new RecipeIngredient(ingredient.id(), DEFAULT_QUANTITY)),
                DEFAULT_SERVINGS
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