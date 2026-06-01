package be.xplore.cookbook.rest.controller.ingredient.ingredientController;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.ingredient.request.UpdateIngredientDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdateIngredientTests extends BaseIntegrationTest {

    private static final String ORIGINAL_NAME = "Original";
    private static final Unit ORIGINAL_UNIT = Unit.CUP;
    private static final Category ORIGINAL_CATEGORY = Category.GRAIN;

    private static final String UPDATED_NAME = "Ingredient";
    private static final Unit UPDATED_UNIT = Unit.GRAM;
    private static final List<Category> UPDATED_CATEGORIES = List.of(Category.DAIRY);

    @Override
    protected String[] getTablesToClear() {
        return new String[]{
                "ingredient_categories",
                "ingredients",
                "users"
        };
    }

    @Test
    void updateIngredient_shouldReturn204_whenRequestIsValid() throws Exception {
        User user = createUser();
        Ingredient ingredient = createAndSaveIngredient(ORIGINAL_NAME, ORIGINAL_UNIT, ORIGINAL_CATEGORY, user);

        UpdateIngredientDto dto = new UpdateIngredientDto(UPDATED_NAME, UPDATED_UNIT, UPDATED_CATEGORIES);
        performUpdateIngredient(ingredient.id().id().toString(), dto)
                .andExpect(status().isNoContent());

        Ingredient updatedIngredient = getIngredientRepository()
                .findById(ingredient.id())
                .orElseThrow();

        assertThat(updatedIngredient.name()).isEqualTo(UPDATED_NAME);
        assertThat(updatedIngredient.defaultUnit()).isEqualTo(UPDATED_UNIT);
        assertThat(updatedIngredient.categories()).isEqualTo(UPDATED_CATEGORIES);
    }

    @Test
    void updateIngredient_shouldReturn404_whenIngredientNotFound() throws Exception {
        createUser();

        UpdateIngredientDto dto = new UpdateIngredientDto(UPDATED_NAME, UPDATED_UNIT, UPDATED_CATEGORIES);

        performUpdateIngredient(UUID.randomUUID().toString(), dto)
                .andExpect(status().isNotFound());
    }

    @Test
    void updateIngredient_shouldReturn404_whenIngredientBelongsToOtherUser() throws Exception {
        createUser();
        User owner = createUserWithId(UserId.create());
        Ingredient ingredient = createAndSaveIngredient(ORIGINAL_NAME, owner);

        UpdateIngredientDto dto = new UpdateIngredientDto(UPDATED_NAME, UPDATED_UNIT, UPDATED_CATEGORIES);

        performUpdateIngredient(ingredient.id().id().toString(), dto)
                .andExpect(status().isNotFound());
    }

    @Test
    void updateIngredient_shouldReturn404_whenIngredientIsGlobal() throws Exception {
        createUser();
        Ingredient ingredient = createAndSaveIngredient(ORIGINAL_NAME);

        UpdateIngredientDto dto = new UpdateIngredientDto(UPDATED_NAME, UPDATED_UNIT, UPDATED_CATEGORIES);

        performUpdateIngredient(ingredient.id().id().toString(), dto)
                .andExpect(status().isNotFound());
    }

    @Test
    void updateIngredient_shouldReturn401_whenNotAuthenticated() throws Exception {
        User user = createUser();
        Ingredient ingredient = createAndSaveIngredient(ORIGINAL_NAME, ORIGINAL_UNIT, ORIGINAL_CATEGORY, user);

        UpdateIngredientDto dto = new UpdateIngredientDto(UPDATED_NAME, UPDATED_UNIT, UPDATED_CATEGORIES);

        getMockMvc().perform(put("/api/ingredients/{id}", ingredient.id().id().toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    private ResultActions performUpdateIngredient(String ingredientId, UpdateIngredientDto dto) throws Exception {
        return getMockMvc().perform(put("/api/ingredients/{id}", ingredientId)
                .with(validJwt())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getMapper().writeValueAsString(dto)));
    }
}
