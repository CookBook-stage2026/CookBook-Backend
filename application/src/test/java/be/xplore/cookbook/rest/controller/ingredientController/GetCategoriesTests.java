package be.xplore.cookbook.rest.controller.ingredientController;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetCategoriesTests extends BaseIntegrationTest {

    @Override
    protected String[] getTablesToClear() {
        return new String[]{};
    }

    @Test
    void getCategories_shouldReturnAllEnumValues() throws Exception {
        performGetCategories()
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(Category.values().length)))
                .andExpect(jsonPath("$", containsInAnyOrder(
                        Arrays.stream(Category.values())
                                .map(Enum::name)
                                .toArray()
                )));
    }

    private ResultActions performGetCategories() throws Exception {
        return getMockMvc().perform(get("/api/ingredients/categories")
                        .with(user("testuser"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }
}
