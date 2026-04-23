package cookbook.stage.backend.api.ingredientController;

import cookbook.stage.backend.service.IngredientService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class GetAllIngredientsTests {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IngredientService ingredientService;
    @BeforeEach
    void clearDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "recipe_ingredients", "recipes", "ingredients");
    }

    @Test
    void getAllIngredients_shouldReturnAllIngredients_whenIngredientsExist() throws Exception {
        // Arrange
        ingredientService.createIngredient("Flour", Unit.GRAM);
        ingredientService.createIngredient("Eggs", Unit.PIECE);

        // Act & Assert
        performGetAllIngredients(DEFAULT_PAGE, DEFAULT_PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value("Flour"))
                .andExpect(jsonPath("$[0].unit").value("GRAM"))
                .andExpect(jsonPath("$[1].id").exists())
                .andExpect(jsonPath("$[1].name").value("Eggs"))
                .andExpect(jsonPath("$[1].unit").value("PIECE"));
    }

    @Test
    void getAllIngredients_shouldReturnEmptyList_whenNoIngredientsExist() throws Exception {
        // Act & Assert
        performGetAllIngredients(DEFAULT_PAGE, DEFAULT_PAGE_SIZE)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllIngredients_shouldReturnPagedIngredients_whenPageSizeIsSmall() throws Exception {
        // Arrange
        ingredientService.createIngredient("Flour", Unit.GRAM);
        ingredientService.createIngredient("Eggs", Unit.PIECE);
        ingredientService.createIngredient("Butter", Unit.GRAM);

        // Act & Assert
        performGetAllIngredients(DEFAULT_PAGE, 2)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getAllIngredients_shouldReturnSecondPage_whenRequestingPageOne() throws Exception {
        // Arrange
        ingredientService.createIngredient("Flour", Unit.GRAM);
        ingredientService.createIngredient("Eggs", Unit.PIECE);
        ingredientService.createIngredient("Butter", Unit.GRAM);

        // Act & Assert
        performGetAllIngredients(1, 2)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Butter"));
    }

    @Test
    void getAllIngredients_shouldReturn400_whenPageIsNegative() throws Exception {
        // Act & Assert
        performGetAllIngredients(-1, DEFAULT_PAGE_SIZE)
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllIngredients_shouldReturn400_whenSizeIsNegative() throws Exception {
        // Act & Assert
        performGetAllIngredients(DEFAULT_PAGE, -1)
                .andExpect(status().isBadRequest());
    }

    private ResultActions performGetAllIngredients(int page, int size) throws Exception {
        return mockMvc.perform(get("/api/ingredients")
                        .with(user("testuser"))
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }
}
