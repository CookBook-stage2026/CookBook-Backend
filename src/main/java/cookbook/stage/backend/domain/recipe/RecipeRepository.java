package cookbook.stage.backend.domain.recipe;

import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.user.UserPreferences;
import cookbook.stage.backend.domain.user.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository {
    Recipe save(Recipe recipe);

    Optional<Recipe> findById(RecipeId id, UserId userId);

    Page<RecipeSummary> findAllSummariesWithFilter(List<IngredientId> ingredientIds, UserPreferences preferences,
                                                   UserId userId, Pageable pageable);

    long count();
}
