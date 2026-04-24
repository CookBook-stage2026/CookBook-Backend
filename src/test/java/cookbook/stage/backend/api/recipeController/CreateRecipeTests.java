package cookbook.stage.backend.api.recipeController;

import cookbook.stage.backend.api.input.CreateRecipeDto;
import cookbook.stage.backend.api.input.CreateRecipeIngredientDto;
import cookbook.stage.backend.api.result.RecipeDto;
import cookbook.stage.backend.domain.ingredient.Category;
import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.ingredient.IngredientRepository;
import cookbook.stage.backend.domain.ingredient.Unit;
import cookbook.stage.backend.domain.recipe.RecipeId;
import cookbook.stage.backend.domain.recipe.RecipeRepository;
import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser
class CreateRecipeTests {

    private static final String DEFAULT_RECIPE_NAME = "Test Name";
    private static final String DEFAULT_RECIPE_DESCRIPTION = "Test Description";
    private static final int DEFAULT_DURATION_IN_MINUTES = 60;
    private static final int DEFAULT_SERVINGS = 2;
    private static final List<String> DEFAULT_STEPS = List.of("This is step 1", "This is step 2");
    private static final double DEFAULT_QUANTITY = 1.0;
    private static final UserId USER_ID = UserId.create();
    private static final String USER_NAME = "username";
    private static final String USER_EMAIL = "user@email.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper mapper;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "recipe_ingredients", "recipe_steps", "recipes", "ingredients", "users");
    }

    @Test
    void createRecipe_shouldReturnRecipe_whenRequestIsValid() throws Exception {
        // Arrange
        Ingredient flour = ingredientRepository.save(new Ingredient(new IngredientId(UUID.randomUUID()),
                "Flour", Unit.GRAM, Category.GRAIN));
        Ingredient eggs = ingredientRepository.save(new Ingredient(new IngredientId(UUID.randomUUID()),
                "Eggs", Unit.PIECE, Category.EGG));

        CreateRecipeDto dto = buildCreateRecipeDto(List.of(
                new CreateRecipeIngredientDto(flour.id().id(), DEFAULT_QUANTITY),
                new CreateRecipeIngredientDto(eggs.id().id(), 2.0)
        ));

        User user = createUser();

        // Act & Assert
        MvcResult result = performCreateRecipeWithValidJwt(dto)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(DEFAULT_RECIPE_NAME))
                .andExpect(jsonPath("$.description").value(DEFAULT_RECIPE_DESCRIPTION))
                .andExpect(jsonPath("$.durationInMinutes").value(DEFAULT_DURATION_IN_MINUTES))
                .andExpect(jsonPath("$.steps[0]").value(DEFAULT_STEPS.get(0)))
                .andExpect(jsonPath("$.steps[1]").value(DEFAULT_STEPS.get(1)))
                .andExpect(jsonPath("$.ingredients", hasSize(2)))
                .andExpect(jsonPath("$.ingredients[*].ingredientId", hasItems(
                        flour.id().id().toString(),
                        eggs.id().id().toString()
                )))
                .andExpect(jsonPath("$.ingredients[*].baseQuantity", hasItems(
                        DEFAULT_QUANTITY,
                        2.0
                )))
                .andReturn();

        RecipeDto response = mapper.readValue(result.getResponse().getContentAsString(), RecipeDto.class);

        RecipeId recipeId = new RecipeId(response.id());
        recipeRepository.findById(recipeId, user.getId())
                .orElseThrow(() -> new Exception("Recipe with id " + recipeId + " not found!"));

        assertThat(recipeRepository.count()).isEqualTo(1);
    }

    @Test
    void createRecipe_shouldReturn400_whenRequestInvalid() throws Exception {
        // Arrange
        CreateRecipeDto dto = new CreateRecipeDto(
                null,
                null,
                DEFAULT_DURATION_IN_MINUTES,
                DEFAULT_STEPS,
                List.of(),
                DEFAULT_SERVINGS
        );

        createUser();

        // Act & Assert
        performCreateRecipeWithValidJwt(dto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRecipe_shouldReturn400_whenQuantityIsNegative() throws Exception {
        // Arrange
        Ingredient flour = ingredientRepository.save(new Ingredient(new IngredientId(UUID.randomUUID()),
                "Flour", Unit.GRAM, Category.GRAIN));

        CreateRecipeDto dto = new CreateRecipeDto(
                DEFAULT_RECIPE_NAME,
                DEFAULT_RECIPE_DESCRIPTION,
                DEFAULT_DURATION_IN_MINUTES,
                DEFAULT_STEPS,
                List.of(new CreateRecipeIngredientDto(flour.id().id(), -1.0)),
                DEFAULT_SERVINGS
        );

        createUser();

        // Act & Assert
        performCreateRecipeWithValidJwt(dto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRecipe_shouldReturn400_whenIngredientDoesNotExist() throws Exception {
        // Arrange
        CreateRecipeDto dto = buildCreateRecipeDto(List.of(
                new CreateRecipeIngredientDto(UUID.randomUUID(), DEFAULT_QUANTITY)
        ));

        createUser();

        // Act & Assert
        performCreateRecipeWithValidJwt(dto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRecipe_shouldReturn401_whenNotAuthenticated() throws Exception {
        // Arrange
        Ingredient flour = ingredientRepository.save(new Ingredient(new IngredientId(UUID.randomUUID()),
                "Flour", Unit.GRAM, Category.GRAIN));

        CreateRecipeDto dto = buildCreateRecipeDto(List.of(
                new CreateRecipeIngredientDto(flour.id().id(), DEFAULT_QUANTITY)
        ));

        // Act & Assert
        mockMvc.perform(post("/api/recipes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    private CreateRecipeDto buildCreateRecipeDto(List<CreateRecipeIngredientDto> ingredients) {
        return new CreateRecipeDto(
                DEFAULT_RECIPE_NAME,
                DEFAULT_RECIPE_DESCRIPTION,
                DEFAULT_DURATION_IN_MINUTES,
                DEFAULT_STEPS,
                ingredients,
                DEFAULT_SERVINGS
        );
    }

    private User createUser() {
        return userRepository.save(new User(USER_ID, USER_NAME, USER_EMAIL, List.of()));
    }

    private ResultActions performCreateRecipeWithValidJwt(CreateRecipeDto dto) throws Exception {
        return mockMvc.perform(post("/api/recipes")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andDo(print());
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject(USER_ID.id().toString())
                .claim("email", USER_EMAIL)
                .claim("name", USER_NAME));
    }
}
