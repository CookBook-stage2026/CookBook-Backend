package be.xplore.cookbook.rest.controller;

import be.xplore.cookbook.core.common.Paging;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeDetails;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.command.CreateRecipeCommand;
import be.xplore.cookbook.core.domain.recipe.command.FilterRecipesQuery;
import be.xplore.cookbook.core.domain.recipe.command.FindRecipeByIdQuery;
import be.xplore.cookbook.core.domain.recipe.command.SearchRecipesByNameQuery;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.service.RecipeService;
import be.xplore.cookbook.rest.dto.request.CreateRecipeDto;
import be.xplore.cookbook.rest.dto.request.CreateRecipeIngredientDto;
import be.xplore.cookbook.rest.dto.request.RecipeSearchRequest;
import be.xplore.cookbook.rest.dto.response.PaginatedResponse;
import be.xplore.cookbook.rest.dto.response.RecipeDto;
import be.xplore.cookbook.rest.dto.response.RecipeSummaryDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
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

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeDto createRecipe(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateRecipeDto dto) {
        Map<IngredientId, Double> ingredientQuantities = dto.ingredients().stream()
                .collect(Collectors.toMap(
                        i -> new IngredientId(i.ingredientId()), CreateRecipeIngredientDto::baseQuantity
                ));

        Recipe recipe = recipeService.createRecipe(new CreateRecipeCommand(
                new RecipeDetails(dto.name(), dto.description(), dto.durationInMinutes(), dto.servings(), dto.steps()),
                ingredientQuantities,
                getUserIdFromJwt(jwt)
        ));

        return RecipeDto.fromDomain(recipe);
    }

    @GetMapping("/{id}")
    public RecipeDto getRecipeById(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        Recipe recipe = recipeService.findById(new FindRecipeByIdQuery(new RecipeId(id), getUserIdFromJwt(jwt)));
        return RecipeDto.fromDomain(recipe);
    }

    @PostMapping("/filter")
    public PaginatedResponse<RecipeSummaryDto> filterRecipes(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RecipeSearchRequest request
    ) {
        List<IngredientId> ingredients = request.ingredientIds().stream().map(IngredientId::new).toList();

        var result = recipeService.findAllSummariesWithFilter(new FilterRecipesQuery(
                ingredients, new Paging(
                        request.page(), request.size()), request.shouldApplyPreferences(), getUserIdFromJwt(jwt)
        ));

        return new PaginatedResponse<>(
                result.content().stream().map(RecipeSummaryDto::fromDomain).toList(),
                new PaginatedResponse.PageMetadata(
                        result.pageNumber(), result.pageSize(), result.totalElements(), result.totalPages()
                )
        );
    }

    @GetMapping("/search")
    public List<RecipeSummaryDto> searchRecipeSummaries(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String query
    ) {
        return recipeService.searchSummariesByName(
                new SearchRecipesByNameQuery(new Paging(page, size), getUserIdFromJwt(jwt), query)
        ).stream().map(RecipeSummaryDto::fromDomain).toList();
    }

    /**
     * Gets a suggestion for an enhanced recipe with a new ingredient
     *
     * @param id The id of the recipe to enhance
     * @return the enhanced recipe
     */
    @GetMapping("/{id}/enhance")
    public RecipeDto enhanceRecipe(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {
        Recipe recipe = recipeService.enhanceRecipe(new RecipeId(id), getUserIdFromJwt(jwt));

        return RecipeDto.fromDomain(recipe);
    }

    private UserId getUserIdFromJwt(Jwt jwt) {
        return new UserId(UUID.fromString(jwt.getSubject()));
    }
}
