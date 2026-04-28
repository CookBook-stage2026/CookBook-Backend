package be.xplore.cookbook.controller;

import be.xplore.cookbook.dto.request.CreateRecipeDto;
import be.xplore.cookbook.dto.request.CreateRecipeIngredientDto;
import be.xplore.cookbook.dto.request.RecipeSearchRequest;
import be.xplore.cookbook.dto.response.RecipeDto;
import be.xplore.cookbook.dto.response.RecipeSummaryDto;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeDetails;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.service.RecipeService;
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
     * @param jwt The JWT token of the authenticated user
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
                new RecipeDetails(
                        createRecipeDto.name(),
                        createRecipeDto.description(),
                        createRecipeDto.durationInMinutes(),
                        createRecipeDto.servings(),
                        createRecipeDto.steps()
                ),
                ingredientQuantities,
                UserId.fromJwt(jwt)
        );

        return RecipeDto.fromDomain(recipe);
    }

    /**
     * Gets a recipe by id
     *
     * @param jwt The JWT token of the authenticated user
     * @param id The id of the requested recipe
     * @return Recipe
     */
    @GetMapping("/{id}")
    public RecipeDto getRecipeById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {
        Recipe recipe = recipeService.findById(new RecipeId(id), UserId.fromJwt(jwt));

        return RecipeDto.fromDomain(recipe);
    }

    /**
     * Searches recipes with pagination and optional filtering
     *
     * @param jwt The JWT token of the authenticated user
     * @param request Search criteria containing optional ingredient filter, preference filtering (default true),
     *                page (default 0) and size (default 20)
     * @return Page of recipe summaries
     */
    @PostMapping("/search")
    public Page<RecipeSummaryDto> searchRecipes(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RecipeSearchRequest request
    ) {
        List<IngredientId> ingredients = request.ingredientIds().stream()
                .map(IngredientId::new)
                .toList();

        Pageable pageable = PageRequest.of(request.page(), request.size());
        UserId userId = new UserId(UUID.fromString(jwt.getSubject()));

        return recipeService.findAllSummariesWithFilter(ingredients, pageable,
                         request.shouldApplyPreferences(), userId)
                .map(RecipeSummaryDto::fromDomain);
    }

    /**
     * Searches recipes by name
     *
     * @param page  current page (default 0)
     * @param size  current page size (default 10)
     * @param query letters that have to be in the recipe name
     * @return list of summaries of recipes that contain the query
     */
    @GetMapping("/search")
    public List<RecipeSummaryDto> searchRecipeSummaries(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String query
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return recipeService.searchSummariesByName(pageable,
                new UserId(UUID.fromString(jwt.getSubject())),
                query).stream().map(RecipeSummaryDto::fromDomain).toList();
    }
}
