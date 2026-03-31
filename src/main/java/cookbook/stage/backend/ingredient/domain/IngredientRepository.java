package cookbook.stage.backend.ingredient.domain;

import cookbook.stage.backend.ingredient.shared.IngredientId;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IngredientRepository {
    Ingredient save(Ingredient ingredient);

    List<Ingredient> findAll(Pageable pageable);

    List<Ingredient> findAllByIds(List<IngredientId> ids);
}
