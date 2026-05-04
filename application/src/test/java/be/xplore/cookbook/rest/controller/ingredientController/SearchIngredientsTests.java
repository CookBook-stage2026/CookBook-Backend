package be.xplore.cookbook.rest.controller.ingredientController;

import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.request.IngredientSearchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class SearchIngredientsTests extends BaseIntegrationTest {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int EXPECTED_3_ITEMS = 3;

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"recipe_ingredients", "recipes", "ingredients"};
    }

    @Test
    void searchIngredients_shouldReturnMatchingIngredients_whenQueryMatches() throws Exception {
        // Arrange
        createAndSaveIngredient("All-Purpose Flour");
        createAndSaveIngredient("Almond Flour");
        createAndSaveIngredient("White Sugar");

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
        createAndSaveIngredient("Salt");

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
        createAndSaveIngredient("Red Apple");
        createAndSaveIngredient("Green Apple");
        createAndSaveIngredient("Fuji Apple");

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
    void searchIngredients_shouldBeCaseInsensitive() throws Exception {
        // Arrange
        createAndSaveIngredient("All-Purpose Flour");

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
        createAndSaveIngredient("Flour");
        createAndSaveIngredient("Sugar");
        createAndSaveIngredient("Salt");

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
        Ingredient flour = createAndSaveIngredient("Flour");
        Ingredient sugar = createAndSaveIngredient("Sugar");
        createAndSaveIngredient("Salt");

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
        Ingredient flour = createAndSaveIngredient("Flour");
        createAndSaveIngredient("Sugar");
        createAndSaveIngredient("Salt");

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
        createAndSaveIngredient("Dark Chocolate");
        createAndSaveIngredient("Chocolate Milk");
        createAndSaveIngredient("White Chocolate");

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
        return getMockMvc().perform(post("/api/ingredients/search")
                        .with(user("testuser"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(request)))
                .andDo(print());
    }
}
