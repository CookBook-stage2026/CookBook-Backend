package cookbook.stage.backend.domain.ingredient;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository {
    Ingredient save(Ingredient ingredient);

    Optional<Ingredient> findById(IngredientId id);

    List<Ingredient> findByIds(List<IngredientId> ids);

    List<Ingredient> searchByName(String name, List<IngredientId> selectedIds, Pageable pageable);
}
