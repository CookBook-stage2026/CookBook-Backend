package be.xplore.cookbook.rest.controller.ingredientController;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.request.CreateIngredientDto;
import be.xplore.cookbook.rest.dto.response.IngredientDto;
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
                List.of(Category.GRAIN)
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
                List.of(Category.GRAIN)
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
                List.of()
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
                List.of(Category.GRAIN)
        );

        // Act & Assert
        getMockMvc().perform(post("/api/ingredients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
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
