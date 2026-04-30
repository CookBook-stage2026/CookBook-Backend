package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.common.Paging;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.repository.IngredientRepository;

import java.util.List;

public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<Ingredient> searchByNameExcludingIds(String name, List<IngredientId> selectedIds, Paging pageable) {
        return this.ingredientRepository.searchByNameExcludingIds(name, selectedIds, pageable);
    }
}
