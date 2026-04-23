package cookbook.stage.backend.domain.recipe;

import cookbook.stage.backend.domain.ingredient.IngredientId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository {
    Recipe save(Recipe recipe);

    Optional<Recipe> findById(RecipeId id);

    Page<RecipeSummary> findAllSummariesWithFilter(List<IngredientId> ingredientIds, Pageable pageable);

    long count();
}
