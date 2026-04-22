package cookbook.stage.backend.api.recipeController;

import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.ingredient.IngredientRepository;
import cookbook.stage.backend.domain.ingredient.Unit;
import cookbook.stage.backend.domain.recipe.Recipe;
import cookbook.stage.backend.domain.recipe.RecipeId;
import cookbook.stage.backend.domain.recipe.RecipeIngredient;
import cookbook.stage.backend.domain.recipe.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@SpringBootTest
class GetRecipeByIdTests {

    private static final String DEFAULT_RECIPE_NAME = "Test Name";
    private static final String DEFAULT_RECIPE_DESCRIPTION = "Test Description";
    private static final int DEFAULT_DURATION_IN_MINUTES = 60;
    private static final int DEFAULT_SERVINGS = 2;
    private static final List<String> DEFAULT_STEPS = List.of("This is step 1", "This is step 2");
    private static final double DEFAULT_QUANTITY = 1.0;
    private static final String DEFAULT_INGREDIENT_NAME = "Flour";
    private static final String DEFAULT_INGREDIENT_NAME_2 = "Butter";
    private static final Unit DEFAULT_UNIT = Unit.GRAM;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "recipe_ingredients", "recipe_steps", "recipes", "ingredients");
    }

    @Test
    void getRecipeById_shouldReturnRecipe_whenRecipeExists() throws Exception {
        // Arrange
        Ingredient ingredient1 = saveIngredient(DEFAULT_INGREDIENT_NAME, DEFAULT_UNIT);
        Ingredient ingredient2 = saveIngredient(DEFAULT_INGREDIENT_NAME_2, DEFAULT_UNIT);

        Recipe recipe = recipeRepository.save(new Recipe(
                RecipeId.create(),
                DEFAULT_RECIPE_NAME,
                DEFAULT_RECIPE_DESCRIPTION,
                DEFAULT_DURATION_IN_MINUTES,
                DEFAULT_STEPS,
                List.of(
                        new RecipeIngredient(ingredient1, DEFAULT_QUANTITY),
                        new RecipeIngredient(ingredient2, DEFAULT_QUANTITY)
                ),
                DEFAULT_SERVINGS
        ));

        // Act & Assert
        performGetRecipeById(recipe.getId().id())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(recipe.getId().id().toString()))
                .andExpect(jsonPath("$.name").value(DEFAULT_RECIPE_NAME))
                .andExpect(jsonPath("$.description").value(DEFAULT_RECIPE_DESCRIPTION))
                .andExpect(jsonPath("$.durationInMinutes").value(DEFAULT_DURATION_IN_MINUTES))
                .andExpect(jsonPath("$.servings").value(DEFAULT_SERVINGS))
                .andExpect(jsonPath("$.steps[0]").value(DEFAULT_STEPS.getFirst()))
                .andExpect(jsonPath("$.steps[1]").value(DEFAULT_STEPS.get(1)))
                .andExpect(jsonPath("$.ingredients[*].ingredientId", hasItems(
                        ingredient1.id().id().toString(),
                        ingredient2.id().id().toString()
                )))
                .andExpect(jsonPath("$.ingredients[*].name", hasItems(
                        DEFAULT_INGREDIENT_NAME, DEFAULT_INGREDIENT_NAME_2)))
                .andExpect(jsonPath("$.ingredients[*].baseQuantity", hasItem(DEFAULT_QUANTITY)))
                .andExpect(jsonPath("$.ingredients[*].unit", hasItem(DEFAULT_UNIT.toString())));
    }

    @Test
    void getRecipeById_shouldReturn404_whenRecipeDoesNotExist() throws Exception {
        performGetRecipeById(UUID.randomUUID())
                .andExpect(status().isNotFound());
    }

    @Test
    void getRecipeById_shouldReturn500_whenIngredientNoLongerExists() throws Exception {
        // Arrange
        Recipe recipe = recipeRepository.save(new Recipe(
                RecipeId.create(),
                DEFAULT_RECIPE_NAME,
                DEFAULT_RECIPE_DESCRIPTION,
                DEFAULT_DURATION_IN_MINUTES,
                DEFAULT_STEPS,
                List.of(new RecipeIngredient(
                        new Ingredient(IngredientId.create(), "Ingredient", Unit.GRAM), DEFAULT_QUANTITY)
                ),
                DEFAULT_SERVINGS
        ));

        // Act & Assert
        performGetRecipeById(recipe.getId().id())
                .andExpect(status().isInternalServerError());
    }

    private Ingredient saveIngredient(String name, Unit unit) {
        Ingredient ingredient = new Ingredient(new IngredientId(UUID.randomUUID()), name, unit);
        ingredientRepository.save(ingredient);
        return ingredient;
    }

    private ResultActions performGetRecipeById(UUID id) throws Exception {
        return mockMvc.perform(get("/api/recipes/{id}", id)
                        .with(user("testuser")))
                .andDo(print());
    }
}
