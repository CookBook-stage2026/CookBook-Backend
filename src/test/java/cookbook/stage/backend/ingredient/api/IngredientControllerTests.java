package cookbook.stage.backend.ingredient.api;

import cookbook.stage.backend.ingredient.domain.IngredientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
public class IngredientControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IngredientRepository ingredientRepository;

    @AfterEach
    void tearDown() {
        ingredientRepository.deleteAll();
    }

    @Nested
    class GetAllIngredientsTests {

        @Test
        void getAllIngredients_shouldReturnAllIngredients_whenIngredientsExist() throws Exception {

        }

        @Test
        void getAllIngredients_shouldReturnEmptyList_whenIngredientsDoNotExist() throws Exception {

        }
    }
}
