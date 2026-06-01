package be.xplore.cookbook.rest.controller.recipe;

import be.xplore.cookbook.core.common.Paging;
import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.recipe.RecipeSortingOptions;
import be.xplore.cookbook.core.domain.recipe.command.FilterRecipesQuery;
import be.xplore.cookbook.core.domain.recipe.command.SearchHouseholdRecipesByNameQuery;
import be.xplore.cookbook.core.domain.recipe.command.SearchPersonalRecipesByNameQuery;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.service.recipe.RecipeQueryService;
import be.xplore.cookbook.rest.dto.common.response.PaginatedResponse;
import be.xplore.cookbook.rest.dto.recipe.request.RecipeSearchRequest;
import be.xplore.cookbook.rest.dto.recipe.response.RecipeSummaryDto;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recipes")
public class RecipeDiscoveryController {

    private final RecipeQueryService recipeQueryService;

    public RecipeDiscoveryController(RecipeQueryService recipeQueryService) {
        this.recipeQueryService = recipeQueryService;
    }

    @PostMapping("/filter")
    public PaginatedResponse<RecipeSummaryDto> filterRecipes(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RecipeSearchRequest request
    ) {
        List<IngredientId> ingredients = request.ingredientIds().stream().map(IngredientId::new).toList();

        var result = recipeQueryService.findAllSummariesWithFilter(new FilterRecipesQuery(
                ingredients, new Paging(request.page(), request.size()), request.shouldApplyPreferences(),
                request.includeAccessibleRecipes(), getUserIdFromJwt(jwt), request.sortBy(),
                request.sortDirection()
        ));

        return new PaginatedResponse<>(
                result.content().stream().map(recipe -> RecipeSummaryDto.fromDomain(recipe, getUserIdFromJwt(jwt)))
                        .toList(),
                new PaginatedResponse.PageMetadata(
                        result.pageNumber(), result.pageSize(), result.totalElements(), result.totalPages()
                )
        );
    }

    @GetMapping("/search/personal")
    public List<RecipeSummaryDto> searchPersonalRecipeSummaries(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String query
    ) {
        return recipeQueryService.searchPersonalSummariesByName(
                        new SearchPersonalRecipesByNameQuery(new Paging(page, size), getUserIdFromJwt(jwt), query)
                ).stream()
                .map(recipe -> RecipeSummaryDto.fromDomain(recipe, getUserIdFromJwt(jwt)))
                .toList();
    }

    @GetMapping("/search/households/{householdId}")
    public List<RecipeSummaryDto> searchHouseholdRecipeSummaries(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID householdId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String query
    ) {
        return recipeQueryService.searchHouseholdSummariesByName(
                        new SearchHouseholdRecipesByNameQuery(new Paging(page, size),
                                new HouseholdId(householdId), getUserIdFromJwt(jwt), query)
                ).stream()
                .map(recipe -> RecipeSummaryDto.fromDomain(recipe, getUserIdFromJwt(jwt)))
                .toList();
    }

    @GetMapping("/sorting-options")
    public List<String> getSortingOptions() {
        return Arrays.stream(RecipeSortingOptions.values())
                .map(Enum::name)
                .toList();
    }

    private UserId getUserIdFromJwt(Jwt jwt) {
        return new UserId(UUID.fromString(jwt.getSubject()));
    }
}
