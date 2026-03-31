package cookbook.stage.backend.ingredient.shared;

import org.springframework.modulith.NamedInterface;

import java.util.List;

@NamedInterface
public interface IngredientsApi {
    void assertAllExist(List<IngredientId> ids);
}
