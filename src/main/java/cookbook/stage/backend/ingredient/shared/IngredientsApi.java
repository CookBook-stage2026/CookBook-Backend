package cookbook.stage.backend.ingredient.shared;

import cookbook.stage.backend.ingredient.domain.Ingredient;
import org.springframework.modulith.NamedInterface;

import java.util.List;

@NamedInterface
public interface IngredientsApi {
    List<Ingredient> findAllById(List<IngredientId> ids);
}
