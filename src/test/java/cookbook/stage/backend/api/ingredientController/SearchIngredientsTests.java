package cookbook.stage.backend.api.ingredientController;

import cookbook.stage.backend.api.input.IngredientSearchRequest;
import cookbook.stage.backend.domain.ingredient.Category;
import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.ingredient.IngredientRepository;
import cookbook.stage.backend.domain.ingredient.Unit;
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
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class SearchIngredientsTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper mapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private IngredientRepository ingredientRepository;

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int NUMBER_OF_INGREDIENTS = 12;
    private static final int EXPECTED_3_ITEMS = 3;

    @BeforeEach
    void clearDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "recipe_ingredients", "recipes", "ingredients");
    }

    @Test
    void searchIngredients_shouldReturnMatchingIngredients_whenQueryMatches() throws Exception {
        // Arrange
        seedIngredient("All-Purpose Flour");
        seedIngredient("Almond Flour");
        seedIngredient("White Sugar");

        IngredientSearchRequest dto = new IngredientSearchRequest(
                "Flour",
                List.of(),
                0,
                DEFAULT_PAGE_SIZE
        );

        // Act & Assert
        performSearch(dto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name").exists());
    }

    @Test
    void searchIngredients_shouldReturnEmptyList_whenNoMatchFound() throws Exception {
        // Arrange
        seedIngredient("Salt");

        IngredientSearchRequest dto = new IngredientSearchRequest(
                "Pepper",
                List.of(),
                0,
                DEFAULT_PAGE_SIZE
        );

        // Act & Assert
        performSearch(dto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchIngredients_shouldRespectPagination_whenPageAndSizeProvided() throws Exception {
        // Arrange
        seedIngredient("Red Apple");
        seedIngredient("Green Apple");
        seedIngredient("Fuji Apple");

        IngredientSearchRequest dto1 = new IngredientSearchRequest(
                "Apple",
                List.of(),
                0,
                2
        );

        IngredientSearchRequest dto2 = new IngredientSearchRequest(
                "Apple",
                List.of(),
                1,
                2
        );

        // Act & Assert - Page 0, Size 2
        performSearch(dto1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Act & Assert - Page 1, Size 2
        performSearch(dto2)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchIngredients_shouldUseDefaultPagination_whenParamsOmitted() throws Exception {
        // Arrange
        for (int i = 0; i < NUMBER_OF_INGREDIENTS; i++) {
            seedIngredient("Tomato " + i);
        }

        IngredientSearchRequest dto = new IngredientSearchRequest(
                "Tomato",
                List.of(),
                0,
                DEFAULT_PAGE_SIZE
        );

        // Act & Assert
        performSearch(dto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(DEFAULT_PAGE_SIZE)));
    }

    @Test
    void searchIngredients_shouldBeCaseInsensitive() throws Exception {
        // Arrange
        seedIngredient("All-Purpose Flour");

        IngredientSearchRequest dto = new IngredientSearchRequest(
                "flour",
                List.of(),
                0,
                DEFAULT_PAGE_SIZE
        );

        // Act & Assert
        performSearch(dto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchIngredients_shouldReturnAllIngredients_whenQueryIsNull() throws Exception {
        // Arrange
        seedIngredient("Flour");
        seedIngredient("Sugar");
        seedIngredient("Salt");

        IngredientSearchRequest dto = new IngredientSearchRequest(
                null,
                List.of(),
                0,
                DEFAULT_PAGE_SIZE
        );

        // Act & Assert
        performSearch(dto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(EXPECTED_3_ITEMS)));
    }

    @Test
    void searchIngredients_shouldExcludeSpecifiedIds_whenExcludedIdsProvided() throws Exception {
        // Arrange
        Ingredient flour = new Ingredient(IngredientId.create(), "Flour", Unit.GRAM, Category.GRAIN);
        Ingredient sugar = new Ingredient(IngredientId.create(), "Sugar", Unit.GRAM, Category.SWEETENER);
        Ingredient salt = new Ingredient(IngredientId.create(), "Salt", Unit.GRAM, Category.ADDITIVE);

        ingredientRepository.save(flour);
        ingredientRepository.save(sugar);
        ingredientRepository.save(salt);

        IngredientSearchRequest dto = new IngredientSearchRequest(
                null,
                List.of(flour.id().id(), sugar.id().id()),
                0,
                DEFAULT_PAGE_SIZE
        );

        // Act & Assert
        performSearch(dto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Salt"));
    }

    @Test
    void searchIngredients_shouldExcludeSpecifiedIdsAndUseQuery_whenBothProvided() throws Exception {
        // Arrange
        Ingredient flour = new Ingredient(new IngredientId(UUID.randomUUID()), "Flour", Unit.GRAM, Category.GRAIN);
        Ingredient sugar = new Ingredient(new IngredientId(UUID.randomUUID()), "Sugar", Unit.GRAM, Category.SWEETENER);
        Ingredient salt = new Ingredient(new IngredientId(UUID.randomUUID()), "Salt", Unit.GRAM, Category.ADDITIVE);

        ingredientRepository.save(flour);
        ingredientRepository.save(sugar);
        ingredientRepository.save(salt);

        IngredientSearchRequest dto = new IngredientSearchRequest(
                "Salt",
                List.of(flour.id().id()),
                0,
                DEFAULT_PAGE_SIZE
        );

        // Act & Assert
        performSearch(dto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Salt"));
    }

    @Test
    void searchIngredients_shouldPrioritizeStartsWithMatches_whenQueryMatchesBothStartAndMiddle() throws Exception {
        // Arrange
        seedIngredient("Dark Chocolate");
        seedIngredient("Chocolate Milk");
        seedIngredient("White Chocolate");

        IngredientSearchRequest dto = new IngredientSearchRequest(
                "Choc",
                List.of(),
                0,
                DEFAULT_PAGE_SIZE
        );

        // Act & Assert
        performSearch(dto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(EXPECTED_3_ITEMS)))
                .andExpect(jsonPath("$[0].name").value("Chocolate Milk"));
    }

    private ResultActions performSearch(IngredientSearchRequest request) throws Exception {
        return mockMvc.perform(post("/api/ingredients/search")
                        .with(user("testuser"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print());
    }

    private void seedIngredient(String name) {
        Ingredient ingredient = new Ingredient(new IngredientId(UUID.randomUUID()), name, Unit.GRAM, Category.DAIRY);
        ingredientRepository.save(ingredient);
    }
}
