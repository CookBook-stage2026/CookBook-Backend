package cookbook.stage.backend.recipe.api;

import cookbook.stage.backend.recipe.api.dto.CreateRecipeDto;
import cookbook.stage.backend.recipe.api.dto.RecipeDto;
import cookbook.stage.backend.recipe.application.RecipeService;
import cookbook.stage.backend.recipe.domain.Recipe;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recipes")
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
    public ResponseEntity<RecipeDto> createRecipe(
            @Valid @RequestBody CreateRecipeDto createRecipeDto
    ) {
        Recipe recipe = recipeService.createRecipe(
                createRecipeDto.name(),
                createRecipeDto.description(),
                createRecipeDto.durationInMinutes(),
                createRecipeDto.steps(),
                createRecipeDto.ingredients()
        );

        RecipeDto recipeDto = RecipeDto.fromDomain(recipe);

        return ResponseEntity.ok(recipeDto);
    }
}
