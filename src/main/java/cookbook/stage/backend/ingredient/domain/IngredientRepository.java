package cookbook.stage.backend.ingredient.domain;

import cookbook.stage.backend.ingredient.shared.IngredientId;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository {
    Ingredient save(Ingredient ingredient);

    List<Ingredient> findAll(Pageable pageable);

    Optional<Ingredient> findById(IngredientId id);

    List<Ingredient> findByIds(List<IngredientId> ids);

    List<Ingredient> searchByName(String name, Pageable pageable);
}
