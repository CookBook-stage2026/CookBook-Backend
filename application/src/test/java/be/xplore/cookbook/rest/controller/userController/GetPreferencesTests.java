package be.xplore.cookbook.rest.controller.userController;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetPreferencesTests extends BaseIntegrationTest {

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"user_excluded_categories", "user_excluded_ingredients", "users", "ingredients"};
    }

    @Test
    void getPreferences_shouldReturnPreferences_whenPreferencesAreSet() throws Exception {
        User user = createUser();

        Ingredient ingredient = createAndSaveIngredient("Ingredient", Unit.GRAM, Category.EGG);

        getUserPreferenceRepository().save(new UserPreferences(
                user,
                List.of(Category.DAIRY),
                List.of(ingredient)
        ));

        performGetPreferences()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.excludedCategories[0]").value(Category.DAIRY.name()))
                .andExpect(jsonPath("$.excludedIngredients[0].id").value(ingredient.id().id().toString()));
    }

    @Test
    void getPreferences_shouldReturnEmptyPreferences_whenNoPreferencesAreSet() throws Exception {
        createUser();

        performGetPreferences()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.excludedCategories").isArray())
                .andExpect(jsonPath("$.excludedCategories").isEmpty())
                .andExpect(jsonPath("$.excludedIngredients").isArray())
                .andExpect(jsonPath("$.excludedIngredients").isEmpty());
    }

    @Test
    void getPreferences_shouldReturn401_whenNotAuthenticated() throws Exception {
        getMockMvc().perform(get("/api/users/preferences"))
                .andExpect(status().isUnauthorized());
    }

    private ResultActions performGetPreferences() throws Exception {
        return getMockMvc().perform(get("/api/users/preferences")
                        .with(validJwt())
                        .with(csrf()))
                .andDo(print());
    }
}
