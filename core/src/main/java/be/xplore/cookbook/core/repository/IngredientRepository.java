package be.xplore.cookbook.core.repository;

import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository {
    Ingredient save(Ingredient ingredient);

    Optional<Ingredient> findById(IngredientId id);

    List<Ingredient> findByIds(List<IngredientId> ids);

    List<Ingredient> searchByName(String name, List<IngredientId> selectedIds, Pageable pageable);
}
