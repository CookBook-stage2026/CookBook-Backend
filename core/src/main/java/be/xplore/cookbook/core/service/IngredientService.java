package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.command.SearchIngredientsQuery;
import be.xplore.cookbook.core.repository.IngredientRepository;

import java.util.List;

public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<Ingredient> searchByNameExcludingIds(SearchIngredientsQuery query) {
        return ingredientRepository.searchByNameExcludingIds(query.name(), query.excludedIds(), query.paging());
    }
}
