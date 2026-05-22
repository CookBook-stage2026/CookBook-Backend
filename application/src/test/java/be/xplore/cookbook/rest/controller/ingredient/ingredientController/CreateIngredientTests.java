package be.xplore.cookbook.rest.controller.ingredient.ingredientController;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.ingredient.Macro;
import be.xplore.cookbook.core.domain.ingredient.MacroType;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.ingredient.request.CreateIngredientDto;
import be.xplore.cookbook.rest.dto.ingredient.response.IngredientDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateIngredientTests extends BaseIntegrationTest {
    private static final double DEFAULT_CALORIES = 50;

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"ingredients", "users"};
    }

    @Test
    void createIngredient_shouldReturnIngredient_whenRequestIsValid() throws Exception {
        // Arrange
        CreateIngredientDto dto = new CreateIngredientDto(
                "Flour",
                Unit.GRAM,
                List.of(
                        Category.GRAIN,
                        Category.ADDITIVE
                ),
                List.of(
                        new CreateIngredientDto.MacroRequest(
                                MacroType.CALORIES,
                                DEFAULT_CALORIES
                        )
                )
        );

        UserId userId = UserId.create();
        createUserWithId(userId);

        // Act & Assert
        MvcResult result = performCreateIngredientWithValidJwtWithUserId(dto, userId)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Flour"))
                .andExpect(jsonPath("$.unit").value("GRAM"))
                .andExpect(jsonPath("$.categories", hasItems(
                        "GRAIN",
                        "ADDITIVE"
                )))
                .andReturn();

        IngredientDto response = getMapper().readValue(
                result.getResponse().getContentAsString(),
                IngredientDto.class
        );

        Ingredient ingredient = getIngredientRepository()
                .findById(new IngredientId(response.id()))
                .orElseThrow(() -> new Exception("Ingredient not found"));

        assertThat(ingredient.name()).isEqualTo("Flour");
        assertThat(ingredient.unit()).isEqualTo(Unit.GRAM);
    }

    @Test
    void createIngredient_shouldReturn400_whenUnitIsNull() throws Exception {
        // Arrange
        CreateIngredientDto dto = new CreateIngredientDto(
                "Flour",
                null,
                List.of(Category.GRAIN),
                List.of(
                        new CreateIngredientDto.MacroRequest(
                                MacroType.CALORIES,
                                DEFAULT_CALORIES
                        )
                )
        );

        createUser();

        // Act & Assert
        performCreateIngredientWithValidJwt(dto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createIngredient_shouldReturn400_whenNameIsBlank() throws Exception {
        // Arrange
        CreateIngredientDto dto = new CreateIngredientDto(
                "",
                Unit.GRAM,
                List.of(Category.GRAIN),
                List.of(
                        new CreateIngredientDto.MacroRequest(
                                MacroType.CALORIES,
                                DEFAULT_CALORIES
                        )
                )
        );

        createUser();

        // Act & Assert
        performCreateIngredientWithValidJwt(dto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createIngredient_shouldReturn400_whenNoCategoriesProvided() throws Exception {
        // Arrange
        CreateIngredientDto dto = new CreateIngredientDto(
                "Flour",
                Unit.GRAM,
                List.of(),
                List.of(
                        new CreateIngredientDto.MacroRequest(
                                MacroType.CALORIES,
                                DEFAULT_CALORIES
                        )
                )
        );

        createUser();

        // Act & Assert
        performCreateIngredientWithValidJwt(dto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createIngredient_shouldReturn401_whenNotAuthenticated() throws Exception {
        // Arrange
        CreateIngredientDto dto = new CreateIngredientDto(
                "Flour",
                Unit.GRAM,
                List.of(Category.GRAIN),
                List.of(
                        new CreateIngredientDto.MacroRequest(
                                MacroType.CALORIES,
                                DEFAULT_CALORIES
                        )
                )
        );

        // Act & Assert
        getMockMvc().perform(post("/api/ingredients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createIngredient_shouldReturnCustomIngredient_whenOverwritingGlobalIngredient() throws Exception {
        // Arrange
        createAndSaveIngredient("Flour", Unit.GRAM, Category.GRAIN, null,
                List.of(
                        new Macro(
                                MacroType.CALORIES,
                                DEFAULT_CALORIES
                        )
                ));
        CreateIngredientDto dto = new CreateIngredientDto(
                "Flour",
                Unit.KILOGRAM,
                List.of(
                        Category.GRAIN,
                        Category.ADDITIVE
                ),
                List.of(
                        new CreateIngredientDto.MacroRequest(
                                MacroType.CALORIES,
                                DEFAULT_CALORIES
                        )
                )
        );

        UserId userId = UserId.create();
        createUserWithId(userId);

        // Act & Assert
        MvcResult result = performCreateIngredientWithValidJwtWithUserId(dto, userId)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.unit").value("KILOGRAM"))
                .andReturn();

        IngredientDto response = getMapper().readValue(
                result.getResponse().getContentAsString(),
                IngredientDto.class
        );

        Ingredient ingredient = getIngredientRepository()
                .findById(new IngredientId(response.id()))
                .orElseThrow(() -> new Exception("Ingredient not found"));

        assertThat(ingredient.name()).isEqualTo("Flour");
        assertThat(ingredient.unit()).isEqualTo(Unit.KILOGRAM);
    }

    @Test
    void createIngredient_shouldReturnExistingIngredient_whenUserAlreadyCreatedIt() throws Exception {
        // Arrange
        CreateIngredientDto dto1 = new CreateIngredientDto(
                "Flour",
                Unit.GRAM,
                List.of(Category.GRAIN),
                List.of(
                        new CreateIngredientDto.MacroRequest(
                                MacroType.CALORIES,
                                DEFAULT_CALORIES
                        )
                )
        );

        CreateIngredientDto dto2 = new CreateIngredientDto(
                "Flour",
                Unit.TEASPOON,
                List.of(Category.EGG),
                List.of(
                        new CreateIngredientDto.MacroRequest(
                                MacroType.CALORIES,
                                DEFAULT_CALORIES
                        )
                )
        );

        UserId userId = UserId.create();
        createUserWithId(userId);

        // Act - Original request
        MvcResult firstResult = performCreateIngredientWithValidJwtWithUserId(dto1, userId)
                .andExpect(status().isCreated())
                .andReturn();

        IngredientDto firstResponse = getMapper().readValue(
                firstResult.getResponse().getContentAsString(),
                IngredientDto.class
        );

        // Act - Second request
        MvcResult secondResult = performCreateIngredientWithValidJwtWithUserId(dto2, userId)
                .andExpect(status().isCreated())
                .andReturn();

        IngredientDto secondResponse = getMapper().readValue(
                secondResult.getResponse().getContentAsString(),
                IngredientDto.class
        );

        // Assert
        assertThat(secondResponse.id()).isEqualTo(firstResponse.id());

        Ingredient ingredient = getIngredientRepository()
                .findById(new IngredientId(secondResponse.id()))
                .orElseThrow(() -> new Exception("Ingredient not found"));

        assertThat(ingredient.name()).isEqualTo("Flour");
        assertThat(ingredient.unit()).isEqualTo(Unit.TEASPOON);
    }

    private ResultActions performCreateIngredientWithValidJwt(CreateIngredientDto dto) throws Exception {
        return getMockMvc().perform(post("/api/ingredients")
                .with(validJwt())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getMapper().writeValueAsString(dto)));
    }

    private ResultActions performCreateIngredientWithValidJwtWithUserId(
            CreateIngredientDto dto, UserId userId) throws Exception {
        return getMockMvc().perform(post("/api/ingredients")
                .with(validJwtFromUserId(userId))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getMapper().writeValueAsString(dto)));
    }
}
