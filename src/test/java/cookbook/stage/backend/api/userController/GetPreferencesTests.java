package cookbook.stage.backend.api.userController;

import cookbook.stage.backend.domain.ingredient.Category;
import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.ingredient.IngredientRepository;
import cookbook.stage.backend.domain.ingredient.Unit;
import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.user.UserPreferences;
import cookbook.stage.backend.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class GetPreferencesTests {

    private static final UserId USER_ID = UserId.create();
    private static final String USER_NAME = "username";
    private static final String USER_EMAIL = "user@email.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "user_excluded_categories", "user_excluded_ingredients", "users", "ingredients");
    }

    @Test
    void getPreferences_shouldReturnPreferences_whenPreferencesAreSet() throws Exception {
        // Arrange
        createUser();

        Ingredient ingredient = ingredientRepository.save(
                new Ingredient(IngredientId.create(), "Ingredient", Unit.GRAM, Category.EGG));

        userRepository.updatePreferences(USER_ID, new UserPreferences(
                List.of(Category.DAIRY),
                List.of(ingredient)
        ));

        // Act & Assert
        performGetPreferences()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.excludedCategories[0]").value(Category.DAIRY.name()))
                .andExpect(jsonPath("$.excludedIngredients[0].id").value(ingredient.id().id().toString()));
    }

    @Test
    void getPreferences_shouldReturnEmptyPreferences_whenNoPreferencesAreSet() throws Exception {
        // Arrange
        createUser();

        // Act & Assert
        performGetPreferences()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.excludedCategories").isArray())
                .andExpect(jsonPath("$.excludedCategories").isEmpty())
                .andExpect(jsonPath("$.excludedIngredients").isArray())
                .andExpect(jsonPath("$.excludedIngredients").isEmpty());
    }

    @Test
    void getPreferences_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/users/preferences"))
                .andExpect(status().isUnauthorized());
    }

    private ResultActions performGetPreferences() throws Exception {
        return mockMvc.perform(get("/api/users/preferences")
                        .with(validJwt())
                        .with(csrf()))
                .andDo(print());
    }

    private User createUser() {
        return userRepository.save(new User(USER_ID, USER_NAME, USER_EMAIL, List.of()));
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject(USER_ID.id().toString())
                .claim("email", USER_EMAIL)
                .claim("name", USER_NAME));
    }
}
