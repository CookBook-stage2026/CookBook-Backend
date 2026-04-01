package cookbook.stage.backend.recipe.api;

import cookbook.stage.backend.recipe.api.dto.CreateRecipeDto;
import cookbook.stage.backend.recipe.api.dto.RecipeDto;
import cookbook.stage.backend.recipe.application.RecipeService;
import cookbook.stage.backend.recipe.domain.Ingredient;
import cookbook.stage.backend.recipe.domain.Recipe;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /**
     * Adds a new recipe to the database
     *
     * @param createRecipeDto The new recipe, without an id
     * @return The created recipe with its generated id
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeDto createRecipe(
            @Valid @RequestBody CreateRecipeDto createRecipeDto
    ) {
        List<Ingredient> ingredients = createRecipeDto.ingredients().stream()
                .map(i -> new Ingredient(i.name(), i.quantity(), i.unit()))
                .toList();

        Recipe recipe = recipeService.createRecipe(
                createRecipeDto.name(),
                createRecipeDto.description(),
                createRecipeDto.durationInMinutes(),
                createRecipeDto.steps(),
                ingredients
        );

        return RecipeDto.fromDomain(recipe);
    }
}
