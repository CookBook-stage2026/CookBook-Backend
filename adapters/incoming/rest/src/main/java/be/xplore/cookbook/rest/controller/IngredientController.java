package be.xplore.cookbook.rest.controller;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.service.IngredientService;
import be.xplore.cookbook.rest.dto.request.IngredientSearchRequest;
import be.xplore.cookbook.rest.dto.response.IngredientDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    /**
     * Searches ingredients by name, with pagination (case-insensitive, substring match).
     *
     * @param request Search criteria containing optional query, already selected ids to exclude,
     *                page (default 0) and size (default 10)
     * @return list of ingredients matching the query
     */
    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<IngredientDto> searchIngredients(
            @RequestBody @Valid IngredientSearchRequest request
    ) {
        List<IngredientId> selected = request.alreadySelectedIds().stream()
                .map(IngredientId::new)
                .toList();

        Pageable pageable = PageRequest.of(request.page(), request.size());

        return ingredientService.searchByName(request.query(), selected, pageable).stream()
                .map(IngredientDto::fromDomain)
                .toList();
    }

    /**
     * Returns all available ingredient categories for the frontend.
     */
    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getCategories() {
        return Arrays.stream(Category.values())
                .map(Category::name)
                .toList();
    }
}
