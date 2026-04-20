package cookbook.stage.backend.ingredient.api.ingredientController;

import cookbook.stage.backend.ingredient.domain.Ingredient;
import cookbook.stage.backend.ingredient.domain.IngredientRepository;
import cookbook.stage.backend.ingredient.shared.Unit;
import cookbook.stage.backend.ingredient.shared.IngredientId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class SearchIngredientsTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private IngredientRepository ingredientRepository;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int NUMBER_OF_INGREDIENTS = 12;

    @BeforeEach
    void clearDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "recipe_ingredients", "recipes", "ingredients");
    }

    @Test
    void searchIngredient_shouldReturnMatchingIngredients_whenQueryMatches() throws Exception {
        // Arrange
        seedIngredient("All-Purpose Flour");
        seedIngredient("Almond Flour");
        seedIngredient("White Sugar");

        // Act & Assert
        mockMvc.perform(get("/api/ingredients/search")
                        .param("query", "Flour")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name").exists());
    }

    @Test
    void searchIngredient_shouldReturnEmptyList_whenNoMatchFound() throws Exception {
        // Arrange
        seedIngredient("Salt");

        // Act & Assert
        mockMvc.perform(get("/api/ingredients/search")
                        .param("query", "Pepper")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchIngredient_shouldRespectPagination_whenPageAndSizeProvided() throws Exception {
        // Arrange
        seedIngredient("Red Apple");
        seedIngredient("Green Apple");
        seedIngredient("Fuji Apple");

        // Act & Assert - Page 0, Size 2
        mockMvc.perform(get("/api/ingredients/search")
                        .param("query", "Apple")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Act & Assert - Page 1, Size 2
        mockMvc.perform(get("/api/ingredients/search")
                        .param("query", "Apple")
                        .param("page", "1")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchIngredient_shouldUseDefaultPagination_whenParamsOmitted() throws Exception {
        // Arrange
        for (int i = 0; i < NUMBER_OF_INGREDIENTS; i++) {
            seedIngredient("Tomato " + i);
        }

        // Act & Assert
        mockMvc.perform(get("/api/ingredients/search")
                        .param("query", "Tomato")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(DEFAULT_PAGE_SIZE)));
    }

    @Test
    void searchIngredient_shouldBeCaseInsensitive() throws Exception {
        // Arrange
        seedIngredient("All-Purpose Flour");

        // Act & Assert
        mockMvc.perform(get("/api/ingredients/search")
                        .param("query", "flour")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    private void seedIngredient(String name) {
        Ingredient ingredient = new Ingredient(new IngredientId(UUID.randomUUID()), name, Unit.GRAM);
        ingredientRepository.save(ingredient);
    }
}
