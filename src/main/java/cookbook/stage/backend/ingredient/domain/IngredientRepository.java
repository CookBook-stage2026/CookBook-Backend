package cookbook.stage.backend.ingredient.domain;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IngredientRepository {
    Ingredient save(Ingredient ingredient);

    List<Ingredient> findAll(Pageable pageable);

    void deleteAll();
}
