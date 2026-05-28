package be.xplore.cookbook.rest.controller.ingredient.ingredientController;

import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeleteIngredientTests extends BaseIntegrationTest {

    private static final String NAME = "Ingredient";

    @Override
    protected String[] getTablesToClear() {
        return new String[]{
                "ingredient_categories",
                "ingredients",
                "users"
        };
    }

    @Test
    void deleteIngredient_shouldReturn204_whenIngredientExistsAndBelongsToUser() throws Exception {
        User user = createUser();
        Ingredient ingredient = createAndSaveIngredient(NAME, user);

        performDeleteIngredient(ingredient.id().id(), user.id())
                .andExpect(status().isNoContent());

        boolean exists = getIngredientRepository()
                .findByIdWithoutCategoriesAndUser(ingredient.id())
                .isPresent();

        assertThat(exists).isFalse();
    }

    @Test
    void deleteIngredient_shouldReturn404_whenIngredientDoesNotExist() throws Exception {
        User user = createUser();

        performDeleteIngredient(UUID.randomUUID(), user.id())
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteIngredient_shouldReturn404_whenIngredientBelongsToAnotherUser() throws Exception {
        User user = createUser();
        User owner = createUserWithId(UserId.create());
        Ingredient ingredient = createAndSaveIngredient(NAME, owner);

        performDeleteIngredient(ingredient.id().id(), user.id())
                .andExpect(status().isNotFound());

        boolean exists = getIngredientRepository()
                .findByIdWithoutCategoriesAndUser(ingredient.id())
                .isPresent();

        assertThat(exists).isTrue();
    }

    @Test
    void deleteIngredient_shouldReturn404_whenIngredientIsGlobal() throws Exception {
        User user = createUser();
        Ingredient ingredient = createAndSaveIngredient(NAME);

        performDeleteIngredient(ingredient.id().id(), user.id())
                .andExpect(status().isNotFound());

        boolean exists = getIngredientRepository()
                .findByIdWithoutCategoriesAndUser(ingredient.id())
                .isPresent();

        assertThat(exists).isTrue();
    }

    @Test
    void deleteIngredient_shouldReturn401_whenNotAuthenticated() throws Exception {
        User user = createUser();
        Ingredient ingredient = createAndSaveIngredient(NAME, user);

        getMockMvc().perform(delete("/api/ingredients/{id}", ingredient.id().id())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    private ResultActions performDeleteIngredient(UUID ingredientId, UserId userId) throws Exception {
        return getMockMvc().perform(delete("/api/ingredients/{id}", ingredientId)
                .with(validJwtFromUserId(userId))
                .with(csrf()));
    }
}
