package cookbook.stage.backend.recipe.domain.recipe;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RecipeRepository {
    Recipe save(Recipe recipe);

    Optional<Recipe> findById(RecipeId id);

    Page<RecipeSummary> findAllSummaries(Pageable pageable);

    long count();
}
