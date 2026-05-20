package be.xplore.cookbook.rest.controller.ingredientController;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        createUser();

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
        createUser();

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
        createUser();

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
        createUser();

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
        createUser();

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
        createUser();

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
        createUser();

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
        createUser();

        createAndSaveIngredient("Dark chocolate");
        createAndSaveIngredient("Chocolate milk");
        createAndSaveIngredient("White chocolate");

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
                .andExpect(jsonPath("$[0].name").value("Chocolate milk"));
    }

    @Test
    void searchIngredients_shouldReturnGlobalAndOwnIngredients_whenValidRequest() throws Exception {
        // Arrange
        User user = createUserWithId(UserId.create());
        User otherUser = createUserWithId(UserId.create());

        createAndSaveIngredient("All-Purpose Flour");
        createAndSaveIngredient("Almond Flour", user);
        createAndSaveIngredient("White Sugar", otherUser);

        IngredientSearchRequest dto = new IngredientSearchRequest(
                "",
                List.of(),
                0,
                DEFAULT_PAGE_SIZE
        );

        // Act & Assert
        performSearchWithUserId(dto, user.id())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void searchIngredients_shouldPrioritizeOwnIngredient_whenValidRequest() throws Exception {
        // Arrange
        User user = createUserWithId(UserId.create());

        createAndSaveIngredient("Flour", Unit.CUP, Category.EGG, null);
        createAndSaveIngredient("Flour", Unit.GRAM, Category.DAIRY, user);

        IngredientSearchRequest dto = new IngredientSearchRequest(
                "",
                List.of(),
                0,
                DEFAULT_PAGE_SIZE
        );

        // Act & Assert
        performSearchWithUserId(dto, user.id())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].unit").value("GRAM"));
    }

    private ResultActions performSearch(IngredientSearchRequest request) throws Exception {
        return getMockMvc().perform(post("/api/ingredients/search")
                .with(validJwt())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getMapper().writeValueAsString(request)));
    }

    private ResultActions performSearchWithUserId(IngredientSearchRequest request, UserId userId) throws Exception {
        return getMockMvc().perform(post("/api/ingredients/search")
                .with(validJwtFromUserId(userId))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getMapper().writeValueAsString(request)));
    }
}
