package be.xplore.cookbook.rest.controller.userController;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.request.UpdateUserPreferencesRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdatePreferencesTests extends BaseIntegrationTest {

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"user_excluded_categories", "user_excluded_ingredients", "users", "ingredients"};
    }

    @Test
    void updatePreferences_shouldReturn204_whenPreferencesAreUpdated() throws Exception {
        // Arrange
        User user = createUser();

        Ingredient ingredient = createAndSaveIngredient("Ingredient", Unit.GRAM, Category.EGG);

        UpdateUserPreferencesRequest request = new UpdateUserPreferencesRequest(
                List.of(Category.DAIRY),
                List.of(ingredient.id().id())
        );

        // Act & Assert
        performUpdatePreferences(request)
                .andExpect(status().isNoContent());

        Optional<UserPreferences> preferences = getUserPreferenceRepository().findPreferences(user);

        assertThat(preferences).isPresent();

        assertThat(preferences.get().excludedCategories())
                .containsExactly(Category.DAIRY);

        assertThat(preferences.get().excludedIngredients())
                .hasSize(1)
                .first()
                .extracting(i -> i.id().id())
                .isEqualTo(ingredient.id().id());
    }

    @Test
    void updatePreferences_shouldClearPreferences_whenEmptyListsAreProvided() throws Exception {
        // Arrange
        User user = createUser();

        Ingredient ingredient = createAndSaveIngredient("Ingredient", Unit.GRAM, Category.EGG);

        getUserPreferenceRepository().save(new UserPreferences(
                user,
                List.of(Category.DAIRY),
                List.of(ingredient)
        ));

        UpdateUserPreferencesRequest request = new UpdateUserPreferencesRequest(List.of(), List.of());

        // Act & Assert
        performUpdatePreferences(request)
                .andExpect(status().isNoContent());

        Optional<UserPreferences> preferences = getUserPreferenceRepository().findPreferences(user);

        assertThat(preferences).isPresent();
        assertThat(preferences.get().excludedCategories()).isEmpty();
        assertThat(preferences.get().excludedIngredients()).isEmpty();
    }

    @Test
    void updatePreferences_shouldReturn401_whenNotAuthenticated() throws Exception {
        UpdateUserPreferencesRequest request = new UpdateUserPreferencesRequest(List.of(), List.of());

        getMockMvc().perform(put("/api/users/preferences")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    private ResultActions performUpdatePreferences(UpdateUserPreferencesRequest request) throws Exception {
        return getMockMvc().perform(put("/api/users/preferences")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(request)))
                .andDo(print());
    }
}
