package be.xplore.cookbook.rest.controller.recipeController;

import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.rest.BaseIntegrationTest;
import be.xplore.cookbook.rest.dto.request.CreateRecipeDto;
import be.xplore.cookbook.rest.dto.request.CreateRecipeIngredientDto;
import be.xplore.cookbook.rest.dto.response.RecipeDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateRecipeTests extends BaseIntegrationTest {

    private static final double DEFAULT_QUANTITY = 1.0;
    private static final int MINUTES_IN_HOUR = 60;

    @Override
    protected String[] getTablesToClear() {
        return new String[]{"recipe_ingredients", "recipe_steps", "recipes", "ingredients", "users"};
    }

    @Test
    void createRecipe_shouldReturnRecipe_whenRequestIsValid() throws Exception {
        // Arrange
        Ingredient flour = createAndSaveIngredient("Flour");
        Ingredient eggs = createAndSaveIngredient("Eggs");

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
                .andExpect(jsonPath("$.name").value("Test Name"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.durationInMinutes").value(MINUTES_IN_HOUR))
                .andExpect(jsonPath("$.steps[0]").value("This is step 1"))
                .andExpect(jsonPath("$.steps[1]").value("This is step 2"))
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

        RecipeDto response = getMapper().readValue(result.getResponse().getContentAsString(), RecipeDto.class);

        RecipeId recipeId = new RecipeId(response.id());
        getRecipeRepository().findById(recipeId, user.id())
                .orElseThrow(() -> new Exception("Recipe with id " + recipeId + " not found!"));

        assertThat(getRecipeRepository().count()).isEqualTo(1);
    }

    @Test
    void createRecipe_shouldReturn400_whenRequestInvalid() throws Exception {
        // Arrange
        CreateRecipeDto dto = new CreateRecipeDto(
                null,
                null,
                MINUTES_IN_HOUR,
                List.of("This is step 1", "This is step 2"),
                List.of(),
                2
        );

        createUser();

        // Act & Assert
        performCreateRecipeWithValidJwt(dto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRecipe_shouldReturn400_whenQuantityIsNegative() throws Exception {
        // Arrange
        Ingredient flour = createAndSaveIngredient("Flour");

        CreateRecipeDto dto = new CreateRecipeDto(
                "Test Name",
                "Test Description",
                MINUTES_IN_HOUR,
                List.of("This is step 1", "This is step 2"),
                List.of(new CreateRecipeIngredientDto(flour.id().id(), -1.0)),
                2
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
        Ingredient flour = createAndSaveIngredient("Flour");

        CreateRecipeDto dto = buildCreateRecipeDto(List.of(
                new CreateRecipeIngredientDto(flour.id().id(), DEFAULT_QUANTITY)
        ));

        // Act & Assert
        getMockMvc().perform(post("/api/recipes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    private CreateRecipeDto buildCreateRecipeDto(List<CreateRecipeIngredientDto> ingredients) {
        return new CreateRecipeDto(
                "Test Name",
                "Test Description",
                MINUTES_IN_HOUR,
                List.of("This is step 1", "This is step 2"),
                ingredients,
                2
        );
    }

    private ResultActions performCreateRecipeWithValidJwt(CreateRecipeDto dto) throws Exception {
        return getMockMvc().perform(post("/api/recipes")
                        .with(validJwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(dto)))
                .andDo(print());
    }
}
