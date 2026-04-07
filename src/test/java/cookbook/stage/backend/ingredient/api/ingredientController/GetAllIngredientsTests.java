package cookbook.stage.backend.ingredient.api.ingredientController;

import cookbook.stage.backend.ingredient.application.IngredientService;
import cookbook.stage.backend.ingredient.domain.Unit;
import cookbook.stage.backend.ingredient.domain.IngredientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasSize;
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
    private MockMvc mockMvc;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private IngredientService ingredientService;

    @AfterEach
    void tearDown() {
        ingredientRepository.deleteAll();
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
        ingredientService.createIngredient("Flour",  Unit.GRAM);
        ingredientService.createIngredient("Eggs",   Unit.PIECE);
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
        ingredientService.createIngredient("Flour",  Unit.GRAM);
        ingredientService.createIngredient("Eggs",   Unit.PIECE);
        ingredientService.createIngredient("Butter", Unit.GRAM);

        // Act & Assert
        performGetAllIngredients(1, 2)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Butter"));
    }

    private ResultActions performGetAllIngredients(int page, int size) throws Exception {
        return mockMvc.perform(get("/api/ingredients")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }
}
