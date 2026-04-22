package cookbook.stage.backend.api;

import cookbook.stage.backend.api.input.CreateRecipeDto;
import cookbook.stage.backend.api.input.CreateRecipeIngredientDto;
import cookbook.stage.backend.api.result.RecipeDto;
import cookbook.stage.backend.api.result.RecipeSummaryDto;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.recipe.Recipe;
import cookbook.stage.backend.domain.recipe.RecipeId;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateRecipeDto createRecipeDto
    ) {
        Map<IngredientId, Double> ingredientQuantities = createRecipeDto.ingredients().stream()
                .collect(Collectors.toMap(
                        dto -> new IngredientId(dto.ingredientId()), CreateRecipeIngredientDto::baseQuantity)
                );

        Recipe recipe = recipeService.createRecipe(
                createRecipeDto.name(),
                createRecipeDto.description(),
                createRecipeDto.durationInMinutes(),
                createRecipeDto.steps(),
                ingredientQuantities,
                createRecipeDto.servings(),
                new UserId(UUID.fromString(jwt.getSubject()))
        );

        return RecipeDto.fromDomain(recipe);
    }

    /**
     * Gets a recipe by id
     *
     * @param id The id of the requested recipe
     * @return Recipe
     */
    @GetMapping("/{id}")
    public RecipeDto getRecipeById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {
        Recipe recipe = recipeService.findById(new RecipeId(id), new UserId(UUID.fromString(jwt.getSubject())));

        return RecipeDto.fromDomain(recipe);
    }

    /**
     * Gets all recipes with pagination and optional filtering
     *
     * @param ingredientIds Optional list of ingredient IDs to filter recipes by
     * @param page          current page (default 0)
     * @param size          size of page (default 20)
     * @return List of recipes
     */
    @GetMapping
    public Page<RecipeSummaryDto> getAllRecipes(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) List<UUID> ingredientIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<IngredientId> ingredients = (ingredientIds == null) ? List.of()
                : ingredientIds.stream()
                  .map(IngredientId::new)
                  .toList();

        Pageable pageable = PageRequest.of(page, size);

        return recipeService.findAllSummariesWithFilter(ingredients, pageable, new UserId(UUID.fromString(jwt.getSubject())))
                .map(RecipeSummaryDto::fromDomain);
    }
}
