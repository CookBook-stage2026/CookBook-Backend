package be.xplore.cookbook.rest.controller.ingredient;

import be.xplore.cookbook.core.common.Paging;
import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.ingredient.command.CreateIngredientCommand;
import be.xplore.cookbook.core.domain.ingredient.command.SearchIngredientsQuery;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.service.ingredient.IngredientService;
import be.xplore.cookbook.rest.dto.ingredient.request.CreateIngredientDto;
import be.xplore.cookbook.rest.dto.ingredient.request.IngredientSearchRequest;
import be.xplore.cookbook.rest.dto.ingredient.response.IngredientDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public IngredientDto createIngredient(
            @Valid @RequestBody CreateIngredientDto dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var ingredient = ingredientService.createIngredient(new CreateIngredientCommand(
                dto.name(),
                dto.defaultUnit(),
                dto.categories(),
                getUserIdFromJwt(jwt)
        ));

        return IngredientDto.fromDomain(ingredient);
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<IngredientDto> searchIngredients(
            @RequestBody @Valid IngredientSearchRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        List<IngredientId> excludedIds = request.alreadySelectedIds().stream()
                .map(IngredientId::new)
                .toList();

        return ingredientService.searchByNameExcludingIds(new SearchIngredientsQuery(
                        request.query(), excludedIds, new Paging(request.page(), request.size()),
                        getUserIdFromJwt(jwt)))
                .stream()
                .map(IngredientDto::fromDomain)
                .toList();
    }

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getCategories() {
        return Arrays.stream(Category.values())
                .map(Category::name)
                .toList();
    }

    @GetMapping("/units")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getUnits() {
        return Arrays.stream(Unit.values())
                .map(Unit::name)
                .toList();
    }

    private UserId getUserIdFromJwt(Jwt jwt) {
        return new UserId(UUID.fromString(jwt.getSubject()));
    }
}
