package cookbook.stage.backend.recipe.api;

import cookbook.stage.backend.recipe.api.dto.CreateRecipeDto;
import cookbook.stage.backend.recipe.api.dto.RecipeDto;
import cookbook.stage.backend.recipe.api.dto.RecipeSummaryDto;
import cookbook.stage.backend.recipe.application.RecipeService;
import cookbook.stage.backend.recipe.domain.Ingredient;
import cookbook.stage.backend.recipe.domain.Recipe;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     * Gets all recipes with pagination
     *
     * @param page current page (default 0)
     * @param size size of page (default 20)
     * @return List of recipes
     */
    @GetMapping
    public List<RecipeSummaryDto> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return recipeService.findAllSummaries(pageable).stream()
                .map(RecipeSummaryDto::fromDomain)
                .toList();
    }
}
