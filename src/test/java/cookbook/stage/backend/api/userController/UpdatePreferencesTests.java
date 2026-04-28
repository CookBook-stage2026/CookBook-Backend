package cookbook.stage.backend.api.userController;

import cookbook.stage.backend.api.input.UpdateUserPreferencesRequest;
import cookbook.stage.backend.domain.ingredient.Category;
import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.ingredient.IngredientRepository;
import cookbook.stage.backend.domain.ingredient.Unit;
import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.user.UserPreferenceRepository;
import cookbook.stage.backend.domain.user.UserPreferences;
import cookbook.stage.backend.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class UpdatePreferencesTests {

    private static final UserId USER_ID = UserId.create();
    private static final String USER_NAME = "username";
    private static final String USER_EMAIL = "user@email.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JsonMapper mapper;

    @BeforeEach
    void clearDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "user_excluded_categories", "user_excluded_ingredients", "users", "ingredients");
    }

    @Test
    void updatePreferences_shouldReturn204_whenPreferencesAreUpdated() throws Exception {
        // Arrange
        createUser();

        Ingredient ingredient = ingredientRepository.save(
                new Ingredient(IngredientId.create(), "Ingredient", Unit.GRAM, List.of(Category.EGG)));

        UpdateUserPreferencesRequest request = new UpdateUserPreferencesRequest(
                List.of(Category.DAIRY),
                List.of(ingredient.id().id())
        );

        // Act & Assert
        performUpdatePreferences(request)
                .andExpect(status().isNoContent());

        UserPreferences preferences = userPreferenceRepository.findPreferences(USER_ID);

        assertThat(preferences.excludedCategories())
                .containsExactly(Category.DAIRY);

        assertThat(preferences.excludedIngredients())
                .hasSize(1)
                .first()
                .extracting(i -> i.id().id())
                .isEqualTo(ingredient.id().id());
    }

    @Test
    void updatePreferences_shouldClearPreferences_whenEmptyListsAreProvided() throws Exception {
        // Arrange
        createUser();
        userPreferenceRepository.updatePreferences(USER_ID, new UserPreferences(
                List.of(Category.DAIRY),
                List.of()
        ));

        UpdateUserPreferencesRequest request = new UpdateUserPreferencesRequest(List.of(), List.of());

        // Act & Assert
        performUpdatePreferences(request)
                .andExpect(status().isNoContent());

        UserPreferences preferences = userPreferenceRepository.findPreferences(USER_ID);

        assertThat(preferences.excludedCategories()).isEmpty();
        assertThat(preferences.excludedIngredients()).isEmpty();
    }

    @Test
    void updatePreferences_shouldReturn401_whenNotAuthenticated() throws Exception {
        // Arrange
        UpdateUserPreferencesRequest request = new UpdateUserPreferencesRequest(List.of(), List.of());

        // Act & Assert
        mockMvc.perform(put("/api/users/preferences")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    private ResultActions performUpdatePreferences(UpdateUserPreferencesRequest request) throws Exception {
        return mockMvc.perform(put("/api/users/preferences")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
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
