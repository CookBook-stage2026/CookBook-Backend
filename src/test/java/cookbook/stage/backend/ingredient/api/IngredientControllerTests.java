package cookbook.stage.backend.ingredient.api;

import cookbook.stage.backend.ingredient.api.dto.IngredientDto;
import cookbook.stage.backend.ingredient.domain.Ingredient;
import cookbook.stage.backend.ingredient.domain.IngredientRepository;
import cookbook.stage.backend.ingredient.domain.Unit;
import cookbook.stage.backend.ingredient.shared.IngredientId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private static final String DEFAULT_NAME = "Test Ingredient";
    private static final String DEFAULT_DESCRIPTION = "Test Description";
    private static final Unit DEFAULT_UNIT = Unit.Gram;
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private Ingredient createAndSaveIngredient(String name, String description, Unit unit) {
        Ingredient ingredient = new Ingredient(
                IngredientId.create(),
                name,
                description,
                Optional.of(unit)
        );
        ingredientRepository.save(ingredient);
        return ingredient;
    }

    private IngredientDto buildIngredientDto(String name, String description, Unit unit) {
        return new IngredientDto(null, name, description, unit);
    }

    private ResultActions performGetAllIngredients() throws Exception {
        return mockMvc.perform(get("/api/v1/ingredients")
                        .param("page", String.valueOf(DEFAULT_PAGE))
                        .param("size", String.valueOf(DEFAULT_PAGE_SIZE)));
    }

    private ResultActions performCreateIngredient(IngredientDto dto) throws Exception {
        return mockMvc.perform(post("/api/v1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)));
    }

    @Nested
    class GetAllIngredientsTests {

        @Test
        void getAllIngredients_shouldReturnAllIngredients_whenIngredientsExist() throws Exception {
            // Arrange
            Ingredient ingredient1 = createAndSaveIngredient("Ingredient 1", "Description 1", Unit.Gram);
            Ingredient ingredient2 = createAndSaveIngredient("Ingredient 2", "Description 2", Unit.Milliliter);

            // Act & Assert
            performGetAllIngredients()
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(ingredient1.id().id().toString()))
                    .andExpect(jsonPath("$[0].name").value(ingredient1.name()))
                    .andExpect(jsonPath("$[0].description").value(ingredient1.description()))
                    .andExpect(jsonPath("$[0].unit").value(ingredient1.unit().get().name()))
                    .andExpect(jsonPath("$[1].id").value(ingredient2.id().id().toString()))
                    .andExpect(jsonPath("$[1].name").value(ingredient2.name()))
                    .andExpect(jsonPath("$[1].description").value(ingredient2.description()))
                    .andExpect(jsonPath("$[1].unit").value(ingredient2.unit().get().name()));
        }

        @Test
        void getAllIngredients_shouldReturnEmptyList_whenIngredientsDoNotExist() throws Exception {
            // Act & Assert
            performGetAllIngredients()
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    class CreateIngredientTests {

        @Test
        void createIngredient_shouldReturnIngredient_whenDtoIsValid() throws Exception {
            // Arrange
            IngredientDto dto = buildIngredientDto(DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_UNIT);

            // Act & Assert
            performCreateIngredient(dto)
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
                    .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
                    .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT.name()));

            assertThat(ingredientRepository.findAll(
                    org.springframework.data.domain.PageRequest.of(DEFAULT_PAGE, DEFAULT_PAGE_SIZE))
            ).hasSize(1);
        }

        @Test
        void createIngredient_shouldReturnIngredient_whenUnitIsAbsent() throws Exception {
            // Arrange
            IngredientDto dto = buildIngredientDto(DEFAULT_NAME, DEFAULT_DESCRIPTION, null);

            // Act & Assert
            performCreateIngredient(dto)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
                    .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
                    .andExpect(jsonPath("$.unit").doesNotExist());
        }

        @Test
        void createIngredient_shouldReturn400_whenDtoInvalid() throws Exception {
            // Arrange
            IngredientDto dto = buildIngredientDto(null, DEFAULT_DESCRIPTION, DEFAULT_UNIT);

            // Act & Assert
            performCreateIngredient(dto)
                    .andExpect(status().isBadRequest());
        }
    }
}
