package cookbook.stage.backend.ingredient.shared;

import org.springframework.modulith.NamedInterface;

import java.util.List;

@NamedInterface
public interface IngredientsApi {
    List<IngredientApiDto> getIngredientsByIds(List<IngredientId> ids);
}
