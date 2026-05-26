package be.xplore.cookbook.rest.controller.recipe;

import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeDetails;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.command.ChangeVisibilityCommand;
import be.xplore.cookbook.core.domain.recipe.command.CreateRecipeCommand;
import be.xplore.cookbook.core.domain.recipe.command.DeleteRecipeCommand;
import be.xplore.cookbook.core.domain.recipe.command.EnhanceRecipeQuery;
import be.xplore.cookbook.core.domain.recipe.command.FindRecipeByIdQuery;
import be.xplore.cookbook.core.domain.recipe.command.ImportRecipeCommand;
import be.xplore.cookbook.core.domain.recipe.command.IngredientWithQuantity;
import be.xplore.cookbook.core.domain.recipe.command.UpdateRecipeCommand;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.service.recipe.RecipeCommandService;
import be.xplore.cookbook.core.service.recipe.RecipeQueryService;
import be.xplore.cookbook.rest.dto.recipe.request.ChangeRecipeVisibilityRequest;
import be.xplore.cookbook.rest.dto.recipe.request.CreateRecipeDto;
import be.xplore.cookbook.rest.dto.recipe.request.ImportRecipeRequest;
import be.xplore.cookbook.rest.dto.recipe.request.UpdateRecipeDto;
import be.xplore.cookbook.rest.dto.recipe.response.RecipeDto;
import be.xplore.cookbook.rest.dto.recipe.request.ChangeRecipeVisibilityRequest;
import be.xplore.cookbook.rest.dto.recipe.request.CreateRecipeDto;
import be.xplore.cookbook.rest.dto.recipe.request.ImportRecipeRequest;
import be.xplore.cookbook.rest.dto.recipe.request.RecipeSearchRequest;
import be.xplore.cookbook.rest.dto.recipe.request.UpdateRecipeDto;
import be.xplore.cookbook.rest.dto.common.response.PaginatedResponse;
import be.xplore.cookbook.rest.dto.recipe.response.RecipeDto;
import be.xplore.cookbook.rest.dto.recipe.response.RecipeSummaryDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeCommandService recipeCommandService;
    private final RecipeQueryService recipeQueryService;

    public RecipeController(RecipeCommandService recipeCommandService, RecipeQueryService recipeQueryService) {
        this.recipeCommandService = recipeCommandService;
        this.recipeQueryService = recipeQueryService;
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeDto createRecipe(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateRecipeDto dto
    ) {
        UserId userId = getUserIdFromJwt(jwt);

        List<IngredientWithQuantity> ingredientQuantities = dto.ingredients().stream()
                .map(i -> new IngredientWithQuantity(
                        new IngredientId(i.ingredientId()), i.baseQuantity()))
                .toList();

        Recipe recipe = recipeCommandService.createRecipe(new CreateRecipeCommand(
                new RecipeDetails(dto.name(), dto.description(), dto.durationInMinutes(), dto.servings(), dto.steps()),
                ingredientQuantities,
                dto.isPublic(),
                userId
        ));

        return RecipeDto.fromDomain(recipe, userId);
    }

    @GetMapping("/{id}")
    public RecipeDto getRecipeById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {
        UserId userId = getUserIdFromJwt(jwt);

        Recipe recipe = recipeQueryService.findById(new FindRecipeByIdQuery(new RecipeId(id), userId));

        return RecipeDto.fromDomain(recipe, userId);
    }

    @GetMapping("/{id}/enhance")
    public RecipeDto enhanceRecipe(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {
        UserId userId = getUserIdFromJwt(jwt);

        Recipe recipe = recipeQueryService.enhanceRecipe(new EnhanceRecipeQuery(new RecipeId(id), userId));

        return RecipeDto.fromDomain(recipe, userId);
    }

    @PutMapping("/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRecipe(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRecipeDto dto
    ) {
        List<IngredientWithQuantity> ingredientQuantities = dto.ingredients().stream()
                .map(i -> new IngredientWithQuantity(
                        new IngredientId(i.ingredientId()), i.baseQuantity()))
                .toList();

        recipeCommandService.updateRecipe(new UpdateRecipeCommand(
                new RecipeId(id),
                new RecipeDetails(dto.name(), dto.description(), dto.durationInMinutes(), dto.servings(), dto.steps()),
                ingredientQuantities,
                dto.isPublic(),
                getUserIdFromJwt(jwt)
        ));
    }

    @PostMapping("/import")
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeDto importRecipe(
            @Valid @RequestBody ImportRecipeRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UserId userId = getUserIdFromJwt(jwt);

        Recipe recipe = recipeCommandService.importRecipe((new ImportRecipeCommand(request.url(), userId)));

        return RecipeDto.fromDomain(recipe, userId);
    }

    @PutMapping("/{id}/visibility")
    @Transactional
    public void changeVisibility(
            @PathVariable UUID id,
            @RequestBody ChangeRecipeVisibilityRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        recipeCommandService.changeVisibility(new ChangeVisibilityCommand(
                new RecipeId(id), getUserIdFromJwt(jwt), request.isPublic()));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {
        UserId userId = getUserIdFromJwt(jwt);
        recipeCommandService.deleteRecipe(new DeleteRecipeCommand(new RecipeId(id), userId));
    }

    private UserId getUserIdFromJwt(Jwt jwt) {
        return new UserId(UUID.fromString(jwt.getSubject()));
    }
}
