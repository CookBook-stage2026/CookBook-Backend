package be.xplore.cookbook.rest.controller;

import be.xplore.cookbook.core.common.Paging;
import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.ingredient.command.SearchIngredientsQuery;
import be.xplore.cookbook.core.service.IngredientService;
import be.xplore.cookbook.rest.dto.request.IngredientSearchRequest;
import be.xplore.cookbook.rest.dto.response.IngredientDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<IngredientDto> searchIngredients(@RequestBody @Valid IngredientSearchRequest request) {
        List<IngredientId> excludedIds = request.alreadySelectedIds().stream()
                .map(IngredientId::new)
                .toList();

        return ingredientService.searchByNameExcludingIds(
                new SearchIngredientsQuery(request.query(), excludedIds, new Paging(request.page(), request.size()))
        ).stream().map(IngredientDto::fromDomain).toList();
    }

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getCategories() {
        return Arrays.stream(Category.values())
                .map(Category::name)
                .toList();
    }
}
